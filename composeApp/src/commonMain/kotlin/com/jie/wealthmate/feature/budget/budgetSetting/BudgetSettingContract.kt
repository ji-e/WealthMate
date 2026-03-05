package com.jie.wealthmate.feature.budget.budgetSetting

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.BudgetWithDetails

data class BudgetSettingUiState(
    val isDataChanged: Boolean = false,
    val yearList: List<String> = emptyList(),
    val selectedYear: String = "",
    val monthBudgets: List<MonthBudgetGroup> = emptyList(),
    val yearlySummary: YearlySummary = YearlySummary(),
) : BaseUiState

data class MonthBudgetGroup(
    val yearMonth: String,
    val budgets: List<BudgetWithDetails>,
    val actualIncome: Long = 0,
    val actualSaving: Long = 0,
    val actualExpense: Long = 0,
)

data class YearlySummary(
    val actualIncome: Long = 0,
    val targetIncome: Long = 0,
    val actualSaving: Long = 0,
    val targetSaving: Long = 0,
    val actualExpense: Long = 0,
    val targetExpense: Long = 0,
) {
    val expenseRate: Int?
        get() = if (actualIncome > 0) ((actualExpense.toFloat() / actualIncome) * 100).toInt() else null

    val balance: Long
        get() = actualIncome - actualExpense - actualSaving
}
