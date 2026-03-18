package com.jie.wealthmate.vo

data class PaymentMethodDiffInfoVo(
    val id: String?,
    val label: String,
    val groupLabel: String?,
    val currentAmount: Long,
    val diffAmount: Long,
    val ratio: Float = 0f
) {
    val lastAmount: Long = currentAmount - diffAmount
}
