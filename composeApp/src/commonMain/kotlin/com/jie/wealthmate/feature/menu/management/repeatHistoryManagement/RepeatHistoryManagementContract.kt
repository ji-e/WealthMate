package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum

data class RepeatHistoryManagementUiState(
    val repeatHistoryItems: List<RepeatCycleWithDetails> = emptyList(),
    val currentTab: LargeCategoryEnum = LargeCategoryEnum.INCOME,
) : BaseUiState {
    val filteredItems: List<RepeatCycleWithDetails>
        get() = repeatHistoryItems.filter { it.category?.largeCategory == currentTab.name }
}
