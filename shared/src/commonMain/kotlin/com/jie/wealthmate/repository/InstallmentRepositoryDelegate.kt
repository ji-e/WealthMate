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

    override suspend fun insertInstallment(installment: InstallmentEntity) =
        d.dualCall { it.insertInstallment(installment) }

    override suspend fun updateInstallment(installment: InstallmentEntity) =
        d.dualCall { it.updateInstallment(installment) }

    override suspend fun deleteInstallment(installment: String) =
        d.dualCall { it.deleteInstallment(installment) }

    override fun getInstallments(): Flow<List<InstallmentEntity>> =
        d.flatFlow { it.getInstallments() }

    // ✅ 복원용 메서드들 구현
    override suspend fun getAllInstallmentsList(): List<InstallmentEntity> = d.call { it.getAllInstallmentsList() }
    override suspend fun insertInstallments(installments: List<InstallmentEntity>) = d.dualCall { it.insertInstallments(installments) }
    override suspend fun deleteAllInstallments() = d.dualCall { it.deleteAllInstallments() }

    // ✅ 핵심 복원 로직: Firestore -> Local
    override suspend fun syncRemoteToLocal() {
        d.restore { special, normal ->
            val remoteData = special.getAllInstallmentsList()
            if (remoteData.isNotEmpty()) {
                normal.deleteAllInstallments()
                normal.insertInstallments(remoteData)
            }
        }
    }
}
