package com.jie.wealthmate.database

import com.jie.wealthmate.repository.GoogleRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.time.Clock

// commonMain/kotlin/DatabaseSyncManager.kt

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

    private val SYNC_FILE_NAME = "wealthmate_sync_data.json"

    /**
     * 로컬의 데이터를 JSON으로 변환하여 Google Drive에 업로드합니다.
     * 모든 테이블의 데이터를 추출하여 병합용 페이로드를 생성합니다.
     */
    suspend fun syncToCloud(accessToken: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val db = databaseProvider.database
            
            // 1. 모든 테이블 데이터 수집 (전체 데이터를 대상으로 병합 수행)
            val payload = SyncPayload(
                lastSyncTime = Clock.System.now().toEpochMilliseconds(),
                histories = db.historyDao().getChangesSince(0),
                categories = db.categoryDao().getChangesSince(0),
                installments = db.installmentDao().getChangesSince(0),
                repeatCycles = db.repeatCycleDao().getChangesSince(0),
                paymentMethods = db.paymentMethodDao().getChangesSince(0),
                paymentMethodGroups = db.paymentMethodGroupDao().getChangesSince(0)
            )

            // 2. JSON 직렬화 및 바이트 변환
            val jsonString = json.encodeToString(payload)
            val bytes = jsonString.encodeToByteArray()

            // 3. Google Drive 업로드
            googleRepository.uploadDatabase(
                accessToken = accessToken,
                dbBytes = bytes,
                fileName = SYNC_FILE_NAME
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Google Drive에서 JSON 데이터를 다운로드하여 로컬 DB와 병합합니다.
     * ID(UUID)가 같으면 최신 데이터로 업데이트하고, 없으면 새로 삽입합니다.
     */
    suspend fun syncFromCloud(accessToken: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // 1. 클라우드에서 데이터 다운로드
            val bytes = googleRepository.downloadDatabase(
                accessToken = accessToken,
                fileName = SYNC_FILE_NAME
            )
            
            val jsonString = bytes.decodeToString()
            val payload = json.decodeFromString<SyncPayload>(jsonString)
            
            // 2. 로컬 DB와 병합 (UPSERT)
            val db = databaseProvider.database
            
            // 외래 키 제약 조건을 고려하여 부모 테이블부터 삽입
            payload.categories.let { db.categoryDao().upsertAll(it) }
            payload.paymentMethodGroups.let { db.paymentMethodGroupDao().upsertAll(it) }
            payload.paymentMethods.let { db.paymentMethodDao().upsertAll(it) }
            payload.repeatCycles.let { db.repeatCycleDao().upsertAll(it) }
            payload.installments.let { db.installmentDao().upsertAll(it) }
            payload.histories.let { db.historyDao().upsertAll(it) }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
