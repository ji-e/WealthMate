package com.jie.wealthmate.repository

import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.eneity.InstallmentEntity
import kotlinx.coroutines.flow.Flow

class InstallmentRepositoryDelegate(
    accountProvider: AccountProvider,
    private val localRepository: InstallmentRepository,
    private val firestoreRepository: InstallmentRepository,
) : InstallmentRepository {
    private val d = AccountAwareDelegate(
        special = firestoreRepository,
        normal = localRepository,
        accountProvider = accountProvider,
    )

    override suspend fun insertInstallment(installment: InstallmentEntity) = d.current.insertInstallment(installment)

    override suspend fun updateInstallment(installment: InstallmentEntity) = d.current.updateInstallment(installment)

    override suspend fun deleteInstallment(installment: String) = d.current.deleteInstallment(installment)

    override fun getInstallments(): Flow<List<InstallmentEntity>> = d.flatFlow { it.getInstallments() }
}
