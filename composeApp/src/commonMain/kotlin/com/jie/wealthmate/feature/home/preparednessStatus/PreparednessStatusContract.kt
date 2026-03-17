package com.jie.wealthmate.feature.home.preparednessStatus

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum

data class PreparednessStatusUiState(
    val statusType: StatusType = StatusType.MONTH,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    val currentAmount: Long = 0,
    val lastAmount: Long = 0,
    val histories: List<HistoryWithDetails> = emptyList(),
    val isLoading: Boolean = false
) : BaseUiState {
    val diffAmount: Long = currentAmount - lastAmount
    val diffPercentage: Int? = if (lastAmount != 0L) {
        (((currentAmount - lastAmount).toFloat() / lastAmount) * 100).toInt()
    } else null
}
