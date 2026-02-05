package com.jie.wealthmate.database

import com.jie.wealthmate.repository.GoogleRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.time.Clock

/**
 * 증분 동기화(Incremental Sync)를 관리하는 매니저입니다.
 * DB 파일을 통째로 올리는 대신, 각 레코드의 UUID와 updatedAt을 기준으로 JSON 병합을 수행합니다.
 */
class DatabaseSyncManager(
    private val databaseProvider: DatabaseProvider,
    private val googleRepository: GoogleRepository
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val syncFileName = "wealthmate_sync_data.json"

    /**
     * 로컬의 데이터를 JSON으로 변환하여 Google Drive에 업로드합니다. (개인 백업용)
     */
    suspend fun syncToCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val bytes = createSyncPayloadBytes()

            // Google Drive 업로드 (App Data Folder)
            googleRepository.uploadDatabase(
                dbBytes = bytes,
                fileName = syncFileName
            )

            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    /**
     * Google Drive에서 JSON 데이터를 다운로드하여 로컬 DB와 병합합니다. (개인 백업용)
     */
    suspend fun syncFromCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val bytes = googleRepository.downloadDatabase(syncFileName)
            val jsonString = bytes.decodeToString()
            val payload = json.decodeFromString(deserializer = SyncPayload.serializer(), string = jsonString)

            mergePayload(payload)

            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    /**
     * [공유 폴더] 내 데이터를 JSON으로 변환하여 업로드합니다.
     * 파일명 규칙: sync_user_{deviceId}.json
     */
    suspend fun syncToSharedFolder(folderId: String, deviceId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val bytes = createSyncPayloadBytes()
            val fileName = "sync_user_$deviceId.json"

            googleRepository.uploadToSharedFolder(
                folderId = folderId,
                fileName = fileName,
                dbBytes = bytes
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [공유 폴더] 내의 모든 상대방 데이터를 다운로드하여 로컬 DB와 병합합니다.
     */
    suspend fun syncFromSharedFolder(folderId: String, myDeviceId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. 공유 폴더 내의 파일 목록 가져오기
            val fileList = googleRepository.getFilesFromSharedFolder(folderId)

            // 2. 내 파일이 아닌 상대방의 파일들만 필터링
            val otherFiles = fileList?.files?.filter { 
                it.name.startsWith("sync_user_") && !it.name.contains(myDeviceId)
            } ?: emptyList()

            // 3. 각 파일을 다운로드하여 병합
            otherFiles.forEach { file ->
                val bytes = googleRepository.downloadFileById(file.id)
                val jsonString = bytes.decodeToString()
                val payload = json.decodeFromString(SyncPayload.serializer(), jsonString)
                
                mergePayload(payload)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 현재 로컬 DB의 데이터를 추출하여 직렬화된 ByteArray를 생성합니다.
     */
    private suspend fun createSyncPayloadBytes(): ByteArray {
        val db = databaseProvider.database
        val payload = SyncPayload(
            lastSyncTime = Clock.System.now().toEpochMilliseconds(),
            histories = db.historyDao().getChangesSince(lastSync = 0L),
            categories = db.categoryDao().getChangesSince(lastSync = 0L),
            installments = db.installmentDao().getChangesSince(lastSync = 0L),
            repeatCycles = db.repeatCycleDao().getChangesSince(lastSync = 0L),
            paymentMethods = db.paymentMethodDao().getChangesSince(lastSync = 0L),
            paymentMethodGroups = db.paymentMethodGroupDao().getChangesSince(lastSync = 0L)
        )
        return json.encodeToString(SyncPayload.serializer(), payload).encodeToByteArray()
    }

    /**
     * 전달받은 페이로드를 로컬 DB에 Upsert 방식으로 병합합니다.
     */
    private suspend fun mergePayload(payload: SyncPayload) {
        val db = databaseProvider.database
        // 외래 키 제약 조건을 고려하여 순차적 실행
        payload.categories.let { db.categoryDao().upsertAll(it) }
        payload.paymentMethodGroups.let { db.paymentMethodGroupDao().upsertAll(it) }
        payload.paymentMethods.let { db.paymentMethodDao().upsertAll(it) }
        payload.repeatCycles.let { db.repeatCycleDao().upsertAll(it) }
        payload.installments.let { db.installmentDao().upsertAll(it) }
        payload.histories.let { db.historyDao().upsertAll(it) }
    }
}
