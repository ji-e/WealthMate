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
    suspend fun insert(paymentGroup: PaymentMethodGroupEntity)

    @Update
    suspend fun update(paymentGroup: PaymentMethodGroupEntity)

    @Query("UPDATE payment_method_groups SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    @Query("SELECT * FROM payment_method_groups WHERE isDeleted = 0 ORDER BY label ASC")
    fun getAll(): Flow<List<PaymentMethodGroupEntity>>
}