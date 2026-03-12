package com.jie.wealthmate.feature.budget.budgetDetail

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

data class BudgetDetailUiState(
    val selectedMonth: LocalDate,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val totalSaving: Long = 0L,
    val sections: ImmutableList<BudgetSectionVo> = persistentListOf(),
) : BaseUiState

data class BudgetSectionVo(
    val largeCategory: LargeCategoryEnum,
    val totalBudget: Long,
    val totalUsed: Long,
    val items: ImmutableList<CategoryBudgetVo>
)

data class CategoryBudgetVo(
    val category: CategoryVo,
    val budgetAmount: Long,
    val usedAmount: Long,
    val percentage: Int
)
