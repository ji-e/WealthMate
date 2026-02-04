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
     * 로컬의 데이터를 JSON으로 변환하여 Google Drive에 업로드합니다.
     */
    suspend fun syncToCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val db = databaseProvider.database

            // 데이터 추출 (현재 Room KMP의 withTransaction 호환성 이슈를 피하기 위해 직접 호출)
            val payload = SyncPayload(
                lastSyncTime = Clock.System.now().toEpochMilliseconds(),
                histories = db.historyDao().getChangesSince(lastSync = 0L),
                categories = db.categoryDao().getChangesSince(lastSync = 0L),
                installments = db.installmentDao().getChangesSince(lastSync = 0L),
                repeatCycles = db.repeatCycleDao().getChangesSince(lastSync = 0L),
                paymentMethods = db.paymentMethodDao().getChangesSince(lastSync = 0L),
                paymentMethodGroups = db.paymentMethodGroupDao().getChangesSince(lastSync = 0L)
            )

            // JSON 직렬화
            val jsonString = json.encodeToString(serializer = SyncPayload.serializer(), value = payload)
            val bytes = jsonString.encodeToByteArray()

            // Google Drive 업로드
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
     * Google Drive에서 JSON 데이터를 다운로드하여 로컬 DB와 병합합니다.
     */
    suspend fun syncFromCloud(): Result<Unit> = withContext(context = Dispatchers.IO) {
        return@withContext try {
            // 1. 클라우드에서 데이터 다운로드
            val bytes = googleRepository.downloadDatabase(syncFileName)

            val jsonString = bytes.decodeToString()
            val payload = json.decodeFromString(deserializer = SyncPayload.serializer(), string = jsonString)

            val db = databaseProvider.database

            // 2. 로컬 DB와 병합 (외래 키 제약 조건을 고려하여 순차적 실행)
            payload.categories.let {
                db.categoryDao().upsertAll(categories = it)
            }
            payload.paymentMethodGroups.let {
                db.paymentMethodGroupDao().upsertAll(paymentMethodGroups = it)
            }
            payload.paymentMethods.let {
                db.paymentMethodDao().upsertAll(paymentMethods = it)
            }
            payload.repeatCycles.let {
                db.repeatCycleDao().upsertAll(repeatCycles = it)
            }
            payload.installments.let {
                db.installmentDao().upsertAll(installments = it)
            }
            payload.histories.let {
                db.historyDao().upsertAll(histories = it)
            }

            Result.success(value = Unit)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }
}