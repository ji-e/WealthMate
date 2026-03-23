package com.jie.wealthmate.repository

import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import com.jie.wealthmate.database.eneity.PaymentMethodWithGroupEntity
import kotlinx.coroutines.flow.Flow

class PaymentMethodRepositoryDelegate(
    accountProvider: AccountProvider,
    private val localRepository: PaymentMethodRepository,
    private val firestoreRepository: PaymentMethodRepository,
) : PaymentMethodRepository {
    private val d = AccountAwareDelegate(
        special = firestoreRepository,
        normal = localRepository,
        accountProvider = accountProvider,
    )

    override suspend fun insertPaymentMethod(
        paymentMethodLabel: String,
        paymentMethodGroupId: String?,
        paymentMethodGroupLabel: String?,
        sort: Long,
    ) = d.dualCall {
        it.insertPaymentMethod(
            paymentMethodLabel,
            paymentMethodGroupId,
            paymentMethodGroupLabel,
            sort
        )
    }

    override suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity) =
        d.dualCall { it.updatePaymentMethod(paymentMethod) }

    override suspend fun updatePaymentMethodSort(updates: List<Pair<String, Long>>) =
        d.dualCall { it.updatePaymentMethodSort(updates) }

    override suspend fun deletePaymentMethod(paymentMethodId: String) =
        d.dualCall { it.deletePaymentMethod(paymentMethodId) }

    override suspend fun getPaymentMethodById(paymentMethodId: String): PaymentMethodWithGroupEntity? =
        d.call { it.getPaymentMethodById(paymentMethodId) }

    override fun getPaymentMethods(): Flow<List<PaymentMethodWithGroupEntity>> =
        d.flatFlow { it.getPaymentMethods() }

    override suspend fun insertPaymentMethodGroup(label: String) =
        d.dualCall { it.insertPaymentMethodGroup(label) }

    override suspend fun updatePaymentMethodGroup(
        paymentMethodGroupId: String,
        paymentMethodGroupLabel: String,
    ) = d.dualCall {
        it.updatePaymentMethodGroup(
            paymentMethodGroupId,
            paymentMethodGroupLabel
        )
    }

    override suspend fun deletePaymentMethodGroup(paymentMethodGroupId: String) =
        d.dualCall { it.deletePaymentMethodGroup(paymentMethodGroupId) }

    override fun getPaymentMethodGroups(): Flow<List<PaymentMethodGroupEntity>> =
        d.flatFlow { it.getPaymentMethodGroups() }

    // ✅ 복원용 메서드들
    override suspend fun getAllPaymentMethodsList(): List<PaymentMethodEntity> = d.call { it.getAllPaymentMethodsList() }
    override suspend fun getAllPaymentMethodGroupsList(): List<PaymentMethodGroupEntity> = d.call { it.getAllPaymentMethodGroupsList() }
    override suspend fun insertPaymentMethods(methods: List<PaymentMethodEntity>) = d.dualCall { it.insertPaymentMethods(methods) }
    override suspend fun insertPaymentMethodGroups(groups: List<PaymentMethodGroupEntity>) = d.dualCall { it.insertPaymentMethodGroups(groups) }
    override suspend fun deleteAllPaymentMethods() = d.dualCall { it.deleteAllPaymentMethods() }
    override suspend fun deleteAllPaymentMethodGroups() = d.dualCall { it.deleteAllPaymentMethodGroups() }

    // ✅ 핵심 복원 로직: Firestore -> Local
    override suspend fun syncRemoteToLocal() {
        d.restore { special, normal ->
            // 그룹 먼저 복원
            val remoteGroups = special.getAllPaymentMethodGroupsList()
            if (remoteGroups.isNotEmpty()) {
                normal.deleteAllPaymentMethodGroups()
                normal.insertPaymentMethodGroups(remoteGroups)
            }
            
            // 결제 수단 복원
            val remoteMethods = special.getAllPaymentMethodsList()
            if (remoteMethods.isNotEmpty()) {
                normal.deleteAllPaymentMethods()
                normal.insertPaymentMethods(remoteMethods)
            }
        }
    }
}
