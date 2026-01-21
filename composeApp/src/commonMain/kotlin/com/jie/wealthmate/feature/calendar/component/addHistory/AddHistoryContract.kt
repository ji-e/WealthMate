package com.jie.wealthmate.feature.calendar.component.addHistory

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect

data class AddHistoryUiState(
    val isChangedData: Boolean = false
) : BaseUiState

sealed class AddHistoryUiSideEffect : UiSideEffect {

}