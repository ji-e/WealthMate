package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface PaymentMethodGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentMethodGroup: PaymentMethodGroupEntity)

    @Update
    suspend fun update(paymentMethodGroup: PaymentMethodGroupEntity)

    @Query("UPDATE payment_method_groups SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    @Query("SELECT * FROM payment_method_groups WHERE isDeleted = 0 ORDER BY label ASC")
    fun getAll(): Flow<List<PaymentMethodGroupEntity>>

    /**
     * 특정 시점 이후에 변경된 모든 결제 수단 그룹 조회 (삭제된 항목 포함)
     */
    @Query("SELECT * FROM payment_method_groups WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<PaymentMethodGroupEntity>

    /**
     * 클라우드 데이터를 로컬에 병합 (ID가 같으면 덮어쓰기)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(paymentMethodGroups: List<PaymentMethodGroupEntity>)

    /**
     * 모든 결제 수단 그룹 삭제 (백업 복원 시 사용)
     */
    @Query("DELETE FROM payment_method_groups")
    suspend fun deleteAll()
}
