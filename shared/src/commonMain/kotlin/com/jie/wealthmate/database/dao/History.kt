package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Update
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface HistoryDao {
    @Query("SELECT * FROM histories WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllHistories(): Flow<List<HistoryEntity>>

    // ✅ 복원용 추가
    @Query("SELECT * FROM histories WHERE isDeleted = 0 ORDER BY date DESC")
    suspend fun getAllHistoriesList(): List<HistoryEntity>

    @Transaction
    @Query("SELECT * FROM histories WHERE isDeleted = 0 ORDER BY date DESC")
    fun getHistoriesWithDetails(): Flow<List<HistoryWithDetails>>

    @Query("SELECT * FROM histories WHERE id = :id AND isDeleted = 0")
    suspend fun getHistoryById(id: String): HistoryWithDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistories(histories: List<HistoryEntity>)

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Query("UPDATE histories SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteHistory(
        id: String,
        timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    )

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Transaction
    @Query("""
        SELECT * FROM histories 
        WHERE isDeleted = 0 
        AND date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC, createdAt DESC
    """)
    fun getHistoriesByMonth(startDate: Long, endDate: Long): Flow<List<HistoryWithDetails>>

    @Query("""
        SELECT SUM(amount) FROM histories 
        WHERE isDeleted = 0 
        AND date BETWEEN :startDate AND :endDate 
        AND largeCategory = :categoryType
    """)
    fun getSumByMonth(startDate: Long, endDate: Long, categoryType: String): Flow<Long?>

    @Query("SELECT * FROM histories WHERE installmentId = :installmentId AND isDeleted = 0 ORDER BY installmentTime ASC")
    suspend fun getHistoriesByInstallmentId(installmentId: String): List<HistoryEntity>

    @Query("SELECT * FROM histories WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(histories: List<HistoryEntity>)

    @Query("DELETE FROM histories")
    suspend fun deleteAll()

    @Transaction
    @RawQuery
    suspend fun searchHistories(query: RoomRawQuery): List<HistoryWithDetails>
}
