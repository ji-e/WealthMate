package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import com.jie.wealthmate.database.eneity.PaymentMethodWithGroupEntity
import kotlinx.coroutines.flow.Flow


interface PaymentMethodRepository {
    suspend fun insertPaymentMethod(
        paymentMethodLabel: String,
        paymentMethodGroupId: String?,
        paymentMethodGroupLabel: String?,
        sort: Long,
    )

    suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity)
    suspend fun updatePaymentMethodSort(updates: List<Pair<String, Long>>)
    suspend fun deletePaymentMethod(paymentMethodId: String)
    suspend fun getPaymentMethodById(paymentMethodId: String): PaymentMethodWithGroupEntity?
    fun getPaymentMethods(): Flow<List<PaymentMethodWithGroupEntity>>

    suspend fun insertPaymentMethodGroup(label: String)
    suspend fun updatePaymentMethodGroup(paymentMethodGroupId: String, paymentMethodGroupLabel: String)
    suspend fun deletePaymentMethodGroup(paymentMethodGroupId: String)
    fun getPaymentMethodGroups(): Flow<List<PaymentMethodGroupEntity>>

    // ✅ 복원용 추가
    suspend fun getAllPaymentMethodsList(): List<PaymentMethodEntity>
    suspend fun getAllPaymentMethodGroupsList(): List<PaymentMethodGroupEntity>
    suspend fun insertPaymentMethods(methods: List<PaymentMethodEntity>)
    suspend fun insertPaymentMethodGroups(groups: List<PaymentMethodGroupEntity>)
    suspend fun deleteAllPaymentMethods()
    suspend fun deleteAllPaymentMethodGroups()
    suspend fun syncRemoteToLocal()
}
