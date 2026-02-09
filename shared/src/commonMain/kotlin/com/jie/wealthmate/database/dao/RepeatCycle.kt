package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface RepeatCycleDao {
    @Query("SELECT * FROM repeat_cycle WHERE isDeleted = 0")
    fun getRepeatCycles(): Flow<List<RepeatCycleEntity>>

    @Query("SELECT * FROM repeat_cycle WHERE isDeleted = 0")
    fun getRepeatCyclesWithDetail(): Flow<List<RepeatCycleWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repeatCycle: RepeatCycleEntity)

    @Update
    suspend fun update(repeatCycle: RepeatCycleEntity)

    // 반복 중단 시
    @Query("UPDATE repeat_cycle SET isActive = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun deactivate(id: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    @Query("UPDATE repeat_cycle SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun delete(id: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    /**
     * 특정 시점 이후에 변경된 모든 반복 주기 조회 (삭제된 항목 포함)
     */
    @Query("SELECT * FROM repeat_cycle WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<RepeatCycleEntity>

    /**
     * 클라우드 데이터를 로컬에 병합 (ID가 같으면 덮어쓰기)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(repeatCycles: List<RepeatCycleEntity>)

    /**
     * 모든 반복 주기 삭제 (백업 복원 시 사용)
     */
    @Query("DELETE FROM repeat_cycle")
    suspend fun deleteAll()
}
