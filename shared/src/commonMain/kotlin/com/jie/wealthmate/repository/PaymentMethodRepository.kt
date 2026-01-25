package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import kotlinx.coroutines.flow.Flow


interface PaymentMethodRepository {
    suspend fun insertPaymentMethodGroup(label: String)

    suspend fun updatePaymentMethodGroup(paymentMethodGroupId: String, label: String)

    suspend fun deletePaymentMethodGroup(paymentMethodGroupId: String)

    fun getPaymentMethodGroups(): Flow<List<PaymentMethodGroupEntity>>
}