package com.jie.wealthmate.feature.home.component.vo

data class DailyInsightVo(
    val dailyLimit: Long,
    val remainDailyAmount: Long,
    val remainingDays: Int,
    val budgetStatusMessage: String,
    val statusMessage: String,
)