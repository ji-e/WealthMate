package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails

data class RepeatHistoryManagementUiState(
    val repeatHistoryItems: List<RepeatCycleWithDetails> = emptyList(),
) : BaseUiState {

}
