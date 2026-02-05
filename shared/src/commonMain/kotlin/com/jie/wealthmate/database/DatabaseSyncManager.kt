package com.jie.wealthmate.database

import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

/**
 * 가계부 데이터 동기화를 관리하는 매니저입니다.
 */
class DatabaseSyncManager(
    private val databaseProvider: DatabaseProvider,
    private val googleRepository: GoogleRepository,
    private val authRepository: AuthRepository,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val syncFileName = "wealthmate_sync_data.json"

    /**
     * 개인 백업용 업로드 (증분 방식)
     */
    suspend fun syncToCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val bytes = createSyncPayloadBytes(isCloudSync = true)
            googleRepository.uploadDatabase(dbBytes = bytes, fileName = syncFileName)
            
            authRepository.saveLastSyncTime(kotlin.time.Clock.System.now().toEpochMilliseconds())
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    /**
     * 개인 백업 다운로드
     */
    suspend fun syncFromCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val bytes = googleRepository.downloadDatabase(syncFileName)
            val jsonString = bytes.decodeToString()
            val payload = json.decodeFromString(SyncPayload.serializer(), jsonString)

            mergePayload(payload)
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    /**
     * [공유 폴더] 업로드 (증분 방식)
     */
    suspend fun syncToSharedFolder(): Result<Unit> = withContext(Dispatchers.IO) {
        val folderId = authRepository.getSharedFolderId() ?: return@withContext Result.failure(Exception("공유 폴더 ID가 없습니다."))
        val deviceId = authRepository.getDeviceId()
        return@withContext try {
            // 공유 폴더 전용 마지막 동기화 시간 사용
            val bytes = createSyncPayloadBytes(isCloudSync = false)
            val fileName = "sync_user_$deviceId.json"

            googleRepository.uploadToSharedFolder(
                folderId = folderId,
                fileName = fileName,
                dbBytes = bytes
            )

            // 공유 전용 동기화 시간 갱신
            authRepository.saveLastSharedSyncTime(kotlin.time.Clock.System.now().toEpochMilliseconds())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [공유 폴더] 다운로드 및 병합
     */
    suspend fun syncFromSharedFolder(): Result<Unit> = withContext(Dispatchers.IO) {
        val folderId = authRepository.getSharedFolderId() ?: return@withContext Result.failure(Exception("공유 폴더 ID가 없습니다."))
        val myDeviceId = authRepository.getDeviceId()
        return@withContext try {
            val fileList = googleRepository.getFilesFromSharedFolder(folderId)
            val otherFiles = fileList?.files?.filter {
                it.name.startsWith("sync_user_") && !it.name.contains(myDeviceId)
            } ?: emptyList()

            if (otherFiles.isEmpty()) {
                return@withContext Result.success(Unit)
            }

            otherFiles.forEach { file ->
                val bytes = googleRepository.downloadFileById(file.id)
                val jsonString = bytes.decodeToString()
                
                // 역직렬화 및 병합
                val payload = json.decodeFromString(SyncPayload.serializer(), jsonString)
                
                if (payload.histories.isNotEmpty()) {
                    mergePayload(payload)
                    Napier.d("병합 완료: ${file.name} 로부터 ${payload.histories.size}건 수신")
                } else {
                    Napier.d("파일 발견: ${file.name}, 하지만 포함된 데이터가 0건입니다.")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 페이로드 생성
     * @param isCloudSync true면 개인 클라우드용(lastSyncTime), false면 공유 폴더용(lastSharedSyncTime)
     */
    private suspend fun createSyncPayloadBytes(isCloudSync: Boolean): ByteArray {
        val db = databaseProvider.database
        val lastSync = if (isCloudSync) authRepository.getLastSyncTime() else authRepository.getLastSharedSyncTime()
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        
        val payload = SyncPayload(
            lastSyncTime = now,
            histories = db.historyDao().getChangesSince(lastSync = lastSync),
            categories = db.categoryDao().getChangesSince(lastSync = lastSync),
            installments = db.installmentDao().getChangesSince(lastSync = lastSync),
            repeatCycles = db.repeatCycleDao().getChangesSince(lastSync = lastSync),
            paymentMethods = db.paymentMethodDao().getChangesSince(lastSync = lastSync),
            paymentMethodGroups = db.paymentMethodGroupDao().getChangesSince(lastSync = lastSync)
        )
        
        val jsonString = json.encodeToString(SyncPayload.serializer(), payload)
        Napier.d("업로드 페이로드 생성 (${if(isCloudSync) "Cloud" else "Shared"}): histories=${payload.histories.size}건")
        
        return jsonString.encodeToByteArray()
    }

    private suspend fun mergePayload(payload: SyncPayload) {
        val db = databaseProvider.database
        // 1. 카테고리 등 기초 데이터 우선 병합 (외래 키 고려)
        if (payload.categories.isNotEmpty()) db.categoryDao().upsertAll(payload.categories)
        if (payload.paymentMethodGroups.isNotEmpty()) db.paymentMethodGroupDao().upsertAll(payload.paymentMethodGroups)
        if (payload.paymentMethods.isNotEmpty()) db.paymentMethodDao().upsertAll(payload.paymentMethods)
        if (payload.repeatCycles.isNotEmpty()) db.repeatCycleDao().upsertAll(payload.repeatCycles)
        if (payload.installments.isNotEmpty()) db.installmentDao().upsertAll(payload.installments)
        
        // 2. 가계부 내역 병합
        if (payload.histories.isNotEmpty()) db.historyDao().upsertAll(payload.histories)
    }
}
