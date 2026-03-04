package com.jie.wealthmate.database

import com.jie.wealthmate.database.eneity.*
import kotlinx.serialization.Serializable

@Serializable
data class SyncPayload(
    val lastSyncTime: Long = 0L,
    val histories: List<HistoryEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val installments: List<InstallmentEntity> = emptyList(),
    val repeatCycles: List<RepeatCycleEntity> = emptyList(),
    val paymentMethods: List<PaymentMethodEntity> = emptyList(),
    val paymentMethodGroups: List<PaymentMethodGroupEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList()
)
