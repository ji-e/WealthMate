package com.jie.wealthmate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jie.wealthmate.database.eneity.InstallmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(installment: InstallmentEntity)

    @Update
    suspend fun update(installment: InstallmentEntity)

    // 반복 중단 시
    @Query("UPDATE installments SET isDeleted = 1 WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM installments WHERE isDeleted = 0")
    fun getInstallments(): Flow<List<InstallmentEntity>>
}