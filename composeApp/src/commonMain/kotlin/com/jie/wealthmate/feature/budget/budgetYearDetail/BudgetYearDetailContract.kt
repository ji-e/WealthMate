package com.jie.wealthmate.feature.budget.budgetYearDetail

import com.jie.wealthmate.base.BaseUiState

data class BudgetYearDetailUiState(
    val selectedYear: String,
    // 예산 (Budget)
    val totalBudgetIncome: Long = 0,
    val totalBudgetExpense: Long = 0,
    val totalBudgetSaving: Long = 0,
    // 실제 (Actual)
    val totalActualIncome: Long = 0,
    val totalActualExpense: Long = 0,
    val totalActualSaving: Long = 0,
    val totalVariableExpense: Long = 0,
    // 전년도 실제 (Last Year Actual)
    val lastActualIncome: Long = 0,
    val lastActualExpense: Long = 0,
    val lastActualSaving: Long = 0,
    // 월별 데이터
    val monthlyData: List<MonthlyComparison> = emptyList(),
    // 주요 지출 카테고리
    val topExpenseCategories: List<CategoryExpense> = emptyList(),
    // 고정 지출 데이터
    val fixedExpenses: List<FixedExpense> = emptyList()
) : BaseUiState

data class MonthlyComparison(
    val month: Int,
    val income: Long,
    val expense: Long,
    val fixedExpense: Long,
    val saving: Long
)

data class CategoryExpense(
    val name: String,
    val amount: Long,
    val icon: String = "💸",
    val ratio: Float // 전체 지출 대비 비중 (0.0 ~ 1.0)
)

data class FixedExpense(
    val name: String,
    val amount: Long,
    val icon: String = "💸"
)
