package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jie.wealthmate.database.eneity.InstallmentEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface InstallmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(installment: InstallmentEntity)

    @Update
    suspend fun update(installment: InstallmentEntity)

    // 반복 중단 시
    @Query("UPDATE installments SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun delete(id: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    @Query("SELECT * FROM installments WHERE isDeleted = 0")
    fun getInstallments(): Flow<List<InstallmentEntity>>

    /**
     * 특정 시점 이후에 변경된 모든 할부 내역 조회 (삭제된 항목 포함)
     */
    @Query("SELECT * FROM installments WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<InstallmentEntity>

    /**
     * 클라우드 데이터를 로컬에 병합 (ID가 같으면 덮어쓰기)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(installments: List<InstallmentEntity>)

    /**
     * 모든 할부 내역 삭제 (백업 복원 시 사용)
     */
    @Query("DELETE FROM installments")
    suspend fun deleteAll()
}
