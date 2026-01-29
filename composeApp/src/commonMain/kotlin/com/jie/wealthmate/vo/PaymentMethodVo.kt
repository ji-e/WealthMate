package com.jie.wealthmate.vo

data class PaymentMethodVo(
    val id: String,
    val label: String,
    val groupId: String?,
    val groupLabel: String?,
    val sort: Long,
)
