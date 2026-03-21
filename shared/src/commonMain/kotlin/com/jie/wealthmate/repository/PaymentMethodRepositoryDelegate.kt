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
        sort: Long
    ) = d.current.insertPaymentMethod(paymentMethodLabel, paymentMethodGroupId, paymentMethodGroupLabel, sort)

    override suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity) = d.current.updatePaymentMethod(paymentMethod)

    override suspend fun updatePaymentMethodSort(updates: List<Pair<String, Long>>) = d.current.updatePaymentMethodSort(updates)

    override suspend fun deletePaymentMethod(paymentMethodId: String) = d.current.deletePaymentMethod(paymentMethodId)

    override suspend fun getPaymentMethodById(paymentMethodId: String): PaymentMethodWithGroupEntity? = d.current.getPaymentMethodById(paymentMethodId)

    override fun getPaymentMethods(): Flow<List<PaymentMethodWithGroupEntity>> = d.flatFlow { it.getPaymentMethods() }

    override suspend fun insertPaymentMethodGroup(label: String) = d.current.insertPaymentMethodGroup(label)

    override suspend fun updatePaymentMethodGroup(paymentMethodGroupId: String, paymentMethodGroupLabel: String) = d.current.updatePaymentMethodGroup(paymentMethodGroupId, paymentMethodGroupLabel)

    override suspend fun deletePaymentMethodGroup(paymentMethodGroupId: String) = d.current.deletePaymentMethodGroup(paymentMethodGroupId)

    override fun getPaymentMethodGroups(): Flow<List<PaymentMethodGroupEntity>> = d.flatFlow { it.getPaymentMethodGroups() }
}
