package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface HistoryDao {
    // 모든 내역 조회 (최신순)
    @Query("SELECT * FROM histories WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllHistories(): Flow<List<HistoryEntity>>

    // 상세 정보(카테고리, 결제수단)를 포함한 내역 조회
    @Transaction
    @Query("SELECT * FROM histories WHERE isDeleted = 0 ORDER BY date DESC")
    fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>>

    // 특정 ID의 내역 조회
    @Query("SELECT * FROM histories WHERE id = :id AND isDeleted = 0")
    suspend fun getHistoryById(id: String): HistoryEntity?

    // 데이터 삽입 (이미 존재하면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    // 리스트 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<HistoryEntity>)

    // 업데이트
    @Update
    suspend fun updateHistory(history: HistoryEntity)

    // 논리적 삭제 (isDeleted flag 업데이트)
    @Query("UPDATE histories SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteHistory(
        id: String,
        timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    )

    // 물리적 삭제
    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    /**
     * 특정 기간(시작일 ~ 종료일) 사이의 내역을 상세 정보와 함께 조회
     * @param startDate 해당 월의 시작 타임스탬프
     * @param endDate 해당 월의 마지막 타임스탬프
     */
    @Transaction
    @Query("""
        SELECT * FROM histories 
        WHERE isDeleted = 0 
        AND date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC
    """)
    fun getHistoriesByMonth(startDate: Long, endDate: Long): Flow<List<HistoryWithDetails>>

    /**
     * 특정 월의 총 수입/지출 합계를 계산 (필요 시)
     * amount가 양수/음수로 구분되어 있거나 largeCategory로 구분할 때 사용
     */
    @Query("""
        SELECT SUM(amount) FROM histories 
        WHERE isDeleted = 0 
        AND date BETWEEN :startDate AND :endDate 
        AND largeCategory = :categoryType
    """)
    fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long?>
}