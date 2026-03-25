package com.jie.wealthmate.feature.budget.budgetDetail

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate

data class BudgetDetailUiState(
    val selectedMonth: LocalDate,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val totalSaving: Long = 0L,
    // 지난달 금액 (비율 계산용)
    val lastTotalIncome: Long = 0L,
    val lastTotalExpense: Long = 0L,
    val lastTotalSaving: Long = 0L,
    val sections: ImmutableList<BudgetSectionVo> = persistentListOf(),
    val expandedStates: ImmutableMap<LargeCategoryEnum, Boolean> = persistentMapOf(),
) : BaseUiState

data class BudgetSectionVo(
    val largeCategory: LargeCategoryEnum,
    val totalBudget: Long,
    val totalUsed: Long,
    val groups: ImmutableList<CategoryBudgetGroupVo>
)

data class CategoryBudgetGroupVo(
    val category: CategoryVo,
    val totalBudget: Long,
    val totalUsed: Long,
    val percentage: Int,
    val tagBudgets: ImmutableList<CategoryBudgetVo>
)

data class CategoryBudgetVo(
    val category: CategoryVo,
    val tag: CategoryTagVo? = null,
    val budgetAmount: Long,
    val usedAmount: Long,
    val percentage: Int
)
