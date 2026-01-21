package com.jie.wealthmate.feature.calendar.component.addHistory

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect

data class AddHistoryUiState(
    val temp: String = "",
) : BaseUiState

sealed class AddHistoryUiSideEffect : UiSideEffect {

}