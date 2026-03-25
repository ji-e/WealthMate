package com.jie.wealthmate.feature.home.categoryExpenses

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.datetime.LocalDate

data class CategoryExpensesUiState(
    val statusType: StatusType = StatusType.MONTH,
    val selectedMonth: LocalDate? = null,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    val category: CategoryVo? = null,
    val totalAmount: Long = 0,
    val lastTotalAmount: Long = 0,
    val histories: List<HistoryWithDetails> = emptyList(),
    val isPagingLoading: Boolean = false,
    val isLastPage: Boolean = false,
    val page: Int = 0,
    val isHome: Boolean = true
) : BaseUiState {
    val diffAmount: Long = totalAmount - lastTotalAmount

    val groupedHistories: List<Pair<LocalDate, List<HistoryWithDetails>>> = histories
        .groupBy { it.history.date.toLocalDate() }
        .toList()
        .sortedByDescending { it.first }
}
