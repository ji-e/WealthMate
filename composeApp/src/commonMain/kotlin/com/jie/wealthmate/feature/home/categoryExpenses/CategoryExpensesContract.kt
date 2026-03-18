package com.jie.wealthmate.feature.home.categoryExpenses

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.datetime.LocalDate

data class CategoryExpensesUiState(
    val statusType: StatusType = StatusType.MONTH,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    val category: CategoryVo? = null,
    val totalAmount: Long = 0,
    val lastTotalAmount: Long = 0,
    val histories: List<HistoryWithDetails> = emptyList(),
    val groupedHistories: List<Pair<LocalDate, List<HistoryWithDetails>>> = emptyList(),
    val isLoading: Boolean = false
) : BaseUiState {
    val diffAmount: Long = totalAmount - lastTotalAmount
}
