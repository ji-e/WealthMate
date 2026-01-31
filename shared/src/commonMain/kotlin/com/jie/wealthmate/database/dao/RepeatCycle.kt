package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepeatCycleDao {
    @Query("SELECT * FROM repeat_cycle WHERE isActive = 1")
    fun getRepeatCycles(): Flow<List<RepeatCycleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repeatCycle: RepeatCycleEntity)

    @Update
    suspend fun update(repeatCycle: RepeatCycleEntity)

    // 반복 중단 시
    @Query("UPDATE repeat_cycle SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: String)
}