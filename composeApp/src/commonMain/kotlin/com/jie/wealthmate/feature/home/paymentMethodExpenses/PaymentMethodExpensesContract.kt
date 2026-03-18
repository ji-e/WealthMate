package com.jie.wealthmate.feature.home.paymentMethodExpenses

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.datetime.LocalDate

data class PaymentMethodExpensesUiState(
    val statusType: StatusType = StatusType.MONTH,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    val paymentMethod: PaymentMethodVo? = null,
    val totalAmount: Long = 0,
    val lastTotalAmount: Long = 0,
    val histories: List<HistoryWithDetails> = emptyList(),
    val groupedHistories: List<Pair<LocalDate, List<HistoryWithDetails>>> = emptyList()
) : BaseUiState {
    val diffAmount: Long = totalAmount - lastTotalAmount
}
