package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jie.wealthmate.database.eneity.PaymentMethodWithGroupEntity
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock

@Dao
interface PaymentMethodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paymentMethod: PaymentMethodEntity)

    @Update
    suspend fun update(paymentMethod: PaymentMethodEntity)

    @Query("UPDATE payment_method SET sort = :sort, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSort(
        id: String,
        sort: Long,
        updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    )

    @Transaction
    suspend fun updatePaymentMethodSort(sortUpdates: List<Pair<String, Long>>) {
        val now = Clock.System.now().toEpochMilliseconds()
        sortUpdates.forEach { (id, sort) ->
            updateSort(id, sort, now)
        }
    }

    @Query("UPDATE payment_method SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    @Query("UPDATE payment_method SET groupId = NULL, groupLabel = NULL, updatedAt = :updatedAt WHERE groupId = :groupId")
    suspend fun clearGroupId(groupId: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    // 단건 조회
    @Query("SELECT * FROM payment_method WHERE id = :id AND isDeleted = 0 LIMIT 1")
    suspend fun getById(id: String): PaymentMethodEntity?

    @Transaction
    @Query("SELECT * FROM payment_method WHERE id = :paymentMethodId")
    suspend fun getPaymentMethodById(paymentMethodId: String): PaymentMethodWithGroupEntity?

    @Transaction
    @Query("SELECT * FROM payment_method WHERE isDeleted = 0 ORDER BY sort ASC")
    fun getAllPaymentMethodsWithGroup(): Flow<List<PaymentMethodWithGroupEntity>>

    @Query("SELECT * FROM payment_method WHERE isDeleted = 0 ORDER BY sort ASC")
    fun getAllPaymentMethods(): Flow<List<PaymentMethodEntity>>

    @Query("SELECT * FROM payment_method WHERE groupId = :groupId AND isDeleted = 0")
    fun getMethodsByGroupId(groupId: String): Flow<List<PaymentMethodEntity>>

    /**
     * 특정 시점 이후에 변경된 모든 결제 수단 조회 (삭제된 항목 포함)
     */
    @Query("SELECT * FROM payment_method WHERE updatedAt > :lastSync")
    suspend fun getChangesSince(lastSync: Long): List<PaymentMethodEntity>

    /**
     * 클라우드 데이터를 로컬에 병합 (ID가 같으면 덮어쓰기)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(paymentMethods: List<PaymentMethodEntity>)
}
