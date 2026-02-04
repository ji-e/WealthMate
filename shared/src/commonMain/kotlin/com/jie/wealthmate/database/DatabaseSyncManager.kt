package com.jie.wealthmate.database

import com.jie.wealthmate.BACK_UP_DB_NAME
import com.jie.wealthmate.DB_NAME
import com.jie.wealthmate.repository.GoogleRepository

// commonMain/kotlin/DatabaseSyncManager.kt

class DatabaseSyncManager(
    private val databaseManager: DatabaseManager,
    private val googleRepository: GoogleRepository
) {
    
    /**
     * 로컬 데이터베이스를 Google Drive에 업로드
     * A 기기에서 내역 작성 후 호출
     */
    suspend fun syncToCloud(accessToken: String): Result<Unit> {
        return try {
            // 1. 데이터베이스 닫기 (WAL 체크포인트 수행)
            databaseManager.closeDatabase()
            
            // 2. 데이터베이스 파일을 바이트 배열로 읽기
            val dbBytes = databaseManager.getDatabaseBytes(DB_NAME)
                ?: return Result.failure(Exception("로컬 데이터베이스를 읽을 수 없습니다"))
            
            // 3. Google Drive에 업로드
            googleRepository.uploadDatabase(
                accessToken = accessToken,
                dbBytes = dbBytes,
                fileName = BACK_UP_DB_NAME
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Google Drive에서 데이터베이스를 다운로드하여 로컬에 저장
     * B 기기에서 내역 확인 전 호출
     */
    suspend fun syncFromCloud(accessToken: String): Result<Unit> {
        return try {
            // 1. 데이터베이스 닫기
            databaseManager.closeDatabase()
            
            // 2. Google Drive에서 다운로드
            val dbBytes = googleRepository.downloadDatabase(
                accessToken = accessToken,
                fileName = BACK_UP_DB_NAME
            )
            
            // 3. 로컬에 저장
            val success = databaseManager.saveDatabaseBytes(
                bytes = dbBytes,
                databaseName = DB_NAME
            )
            
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("로컬 데이터베이스 저장에 실패했습니다"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 양방향 동기화 (충돌 해결 포함)
     * 마지막 수정 시간을 비교하여 최신 버전을 유지
     */
    suspend fun bidirectionalSync(
        accessToken: String,
        lastLocalModified: Long,
        lastCloudModified: Long
    ): Result<SyncResult> {
        return try {
            when {
                lastLocalModified > lastCloudModified -> {
                    // 로컬이 더 최신 -> 업로드
                    syncToCloud(accessToken)
                    Result.success(SyncResult.UPLOADED)
                }
                lastCloudModified > lastLocalModified -> {
                    // 클라우드가 더 최신 -> 다운로드
                    syncFromCloud(accessToken)
                    Result.success(SyncResult.DOWNLOADED)
                }
                else -> {
                    // 동일 -> 동기화 불필요
                    Result.success(SyncResult.UP_TO_DATE)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    enum class SyncResult {
        UPLOADED,
        DOWNLOADED,
        UP_TO_DATE
    }
}