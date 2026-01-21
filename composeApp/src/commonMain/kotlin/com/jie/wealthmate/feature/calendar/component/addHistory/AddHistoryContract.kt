package com.jie.wealthmate.feature.calendar.component.addHistory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect

data class AddHistoryUiState(
    val isChangedData: Boolean = false,
    val amount: TextFieldValue = TextFieldValue(""),
) : BaseUiState

sealed class AddHistoryUiSideEffect : UiSideEffect {

}