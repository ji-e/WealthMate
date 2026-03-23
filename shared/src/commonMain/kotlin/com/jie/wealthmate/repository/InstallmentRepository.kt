package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.InstallmentEntity
import kotlinx.coroutines.flow.Flow


interface InstallmentRepository {

    suspend fun insertInstallment(installment: InstallmentEntity)

    suspend fun updateInstallment(installment: InstallmentEntity)

    suspend fun deleteInstallment(installment: String)

    fun getInstallments(): Flow<List<InstallmentEntity>>

    // ✅ 복원용 추가
    suspend fun getAllInstallmentsList(): List<InstallmentEntity>
    suspend fun insertInstallments(installments: List<InstallmentEntity>)
    suspend fun deleteAllInstallments()
    suspend fun syncRemoteToLocal()
}
