package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.component

data class PaymentMethodItemData(
    val id: String,
    val label: String,
    val groupId: String?,
    val groupLabel: String?,
    val sort: Long,
)