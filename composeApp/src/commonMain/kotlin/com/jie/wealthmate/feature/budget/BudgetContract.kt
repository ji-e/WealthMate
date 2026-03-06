package com.jie.wealthmate.feature.budget

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.budget.component.BudgetOverUsageVo
import com.jie.wealthmate.feature.budget.component.BudgetSummaryVo
import com.jie.wealthmate.utils.today
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

data class BudgetUiState(
    val isDataChanged: Boolean = false,
    val selectedMonth: LocalDate = today,
    val usedAmount: Long = 0L,
    val totalBudgetAmount: Long = 0L,
    val budgetOverItems: ImmutableList<BudgetOverUsageVo> = persistentListOf(),
    val budgetSummaryItems: ImmutableList<BudgetSummaryVo> = persistentListOf(),
) : BaseUiState
