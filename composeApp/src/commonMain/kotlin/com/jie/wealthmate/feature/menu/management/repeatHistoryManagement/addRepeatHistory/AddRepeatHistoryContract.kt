package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails

data class AddRepeatHistoryUiState(
    val repeatHistory: RepeatCycleWithDetails? = null,
) : BaseUiState {

}
