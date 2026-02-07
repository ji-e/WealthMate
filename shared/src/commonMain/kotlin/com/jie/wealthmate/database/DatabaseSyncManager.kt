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

    // --- 개인 클라우드 백업 (Personal Cloud) ---

    /**
     * 개인 백업용 업로드 (증분 방식)
     * 마지막 동기화 시점 이후의 변경사항만 업로드합니다.
     */
    suspend fun syncToCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val lastSync = authRepository.getLastSyncTime()
            val bytes = createSyncPayloadBytes(lastSyncTime = lastSync)
            googleRepository.uploadDatabase(dbBytes = bytes, fileName = syncFileName)

            authRepository.saveLastSyncTime(kotlin.time.Clock.System.now().toEpochMilliseconds())
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    /**
     * 개인 백업용 업로드 (전체 방식)
     * 로컬의 모든 데이터를 클라우드에 업로드합니다.
     */
    suspend fun syncFullToCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val bytes = createSyncPayloadBytes(lastSyncTime = 0L) // 0L은 전체 데이터 추출
            googleRepository.uploadDatabase(dbBytes = bytes, fileName = syncFileName)

            authRepository.saveLastSyncTime(kotlin.time.Clock.System.now().toEpochMilliseconds())
            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    /**
     * 개인 백업 다운로드 (병합)
     */
    suspend fun syncFromCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val bytes = googleRepository.downloadDatabase(syncFileName)
            mergeBackupData(bytes)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    /**
     * 특정 백업 파일 ID를 사용하여 현재 로컬 데이터를 모두 지우고 백업 데이터로 완전히 교체합니다.
     */
    suspend fun restoreFromBackup(fileId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val bytes = googleRepository.downloadFileById(fileId)
            overwriteWithBackupData(bytes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 로컬 데이터를 모두 삭제하고 전달받은 백업 데이터로 완전히 교체합니다.
     */
    suspend fun overwriteWithBackupData(bytes: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val jsonString = bytes.decodeToString()
            val payload = json.decodeFromString(SyncPayload.serializer(), jsonString)
            val db = databaseProvider.database

            // 모든 기존 데이터 삭제
            db.historyDao().deleteAll()
            db.installmentDao().deleteAll()
            db.repeatCycleDao().deleteAll()
            db.paymentMethodDao().deleteAll()
            db.paymentMethodGroupDao().deleteAll()
            db.categoryDao().deleteAll()

            // 백업 데이터로 교체
            mergePayload(payload)

            // 마지막 동기화 시간도 백업 시점으로 맞춤
            authRepository.saveLastSyncTime(payload.lastSyncTime)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 외부에서 전달받은 백업 데이터(ByteArray)를 현재 데이터베이스와 병합합니다.
     */
    suspend fun mergeBackupData(bytes: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val jsonString = bytes.decodeToString()
            val payload = json.decodeFromString(SyncPayload.serializer(), jsonString)

            mergePayload(payload)

            // 병합한 데이터의 시점이 로컬보다 최신인 경우 마지막 동기화 시간 업데이트
            val currentLastSync = authRepository.getLastSyncTime()
            if (payload.lastSyncTime > currentLastSync) {
                authRepository.saveLastSyncTime(payload.lastSyncTime)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 공유 폴더 동기화 (Shared Folder) ---

    /**
     * [공유 폴더] 업로드 (증분 방식)
     * 다른 사용자들에게 변경된 내역만 전달할 때 사용합니다.
     */
    suspend fun syncToSharedFolder(): Result<Unit> = withContext(Dispatchers.IO) {
        val folderId = authRepository.getSharedFolderId() ?: return@withContext Result.failure(Exception("공유 폴더 ID가 없습니다."))
        val deviceId = authRepository.getDeviceId()
        return@withContext try {
            val lastSharedSync = authRepository.getLastSharedSyncTime()
            val bytes = createSyncPayloadBytes(lastSyncTime = lastSharedSync)
            val fileName = "sync_user_$deviceId.json"

            googleRepository.uploadToSharedFolder(folderId = folderId, fileName = fileName, dbBytes = bytes)

            authRepository.saveLastSharedSyncTime(kotlin.time.Clock.System.now().toEpochMilliseconds())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [공유 폴더] 업로드 (전체 방식)
     * 새로운 멤버가 들어왔을 때나, 데이터를 완전히 맞추고 싶을 때 사용합니다.
     */
    suspend fun syncFullToSharedFolder(): Result<Unit> = withContext(Dispatchers.IO) {
        val folderId = authRepository.getSharedFolderId() ?: return@withContext Result.failure(Exception("공유 폴더 ID가 없습니다."))
        val deviceId = authRepository.getDeviceId()
        return@withContext try {
            val bytes = createSyncPayloadBytes(lastSyncTime = 0L) // 전체 데이터 추출
            val fileName = "sync_user_$deviceId.json"

            googleRepository.uploadToSharedFolder(folderId = folderId, fileName = fileName, dbBytes = bytes)

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
                val payload = json.decodeFromString(SyncPayload.serializer(), jsonString)

                if (payload.histories.isNotEmpty() || payload.categories.isNotEmpty()) {
                    mergePayload(payload)
                    Napier.d("병합 완료: ${file.name} 로부터 데이터 수신")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 내부 헬퍼 함수 ---

    /**
     * 동기화용 데이터 페이로드 생성
     * @param lastSyncTime 이 시간 이후의 데이터만 추출합니다. 0L이면 전체 데이터를 추출합니다.
     */
    private suspend fun createSyncPayloadBytes(lastSyncTime: Long): ByteArray {
        val db = databaseProvider.database
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()

        val payload = SyncPayload(
            lastSyncTime = now,
            histories = db.historyDao().getChangesSince(lastSync = lastSyncTime),
            categories = db.categoryDao().getChangesSince(lastSync = lastSyncTime),
            installments = db.installmentDao().getChangesSince(lastSync = lastSyncTime),
            repeatCycles = db.repeatCycleDao().getChangesSince(lastSync = lastSyncTime),
            paymentMethods = db.paymentMethodDao().getChangesSince(lastSync = lastSyncTime),
            paymentMethodGroups = db.paymentMethodGroupDao().getChangesSince(lastSync = lastSyncTime)
        )

        val jsonString = json.encodeToString(SyncPayload.serializer(), payload)
        Napier.d("페이로드 생성 (시점: $lastSyncTime): histories=${payload.histories.size}건")

        return jsonString.encodeToByteArray()
    }

    private suspend fun mergePayload(payload: SyncPayload) {
        val db = databaseProvider.database
        if (payload.categories.isNotEmpty()) db.categoryDao().upsertAll(payload.categories)
        if (payload.paymentMethodGroups.isNotEmpty()) db.paymentMethodGroupDao().upsertAll(payload.paymentMethodGroups)
        if (payload.paymentMethods.isNotEmpty()) db.paymentMethodDao().upsertAll(payload.paymentMethods)
        if (payload.repeatCycles.isNotEmpty()) db.repeatCycleDao().upsertAll(payload.repeatCycles)
        if (payload.installments.isNotEmpty()) db.installmentDao().upsertAll(payload.installments)
        if (payload.histories.isNotEmpty()) db.historyDao().upsertAll(payload.histories)
    }
}
