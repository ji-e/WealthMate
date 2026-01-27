package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum

data class AddHistoryUiState(
    val isDataChanged: Boolean = false,
    val largeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    val content: TextFieldValue = TextFieldValue(""),
    val amount: TextFieldValue = TextFieldValue(""),
) : BaseUiState {
    val isSaveButtonEnable = amount.text.isNotBlank()
}

sealed class AddHistoryUiSideEffect : UiSideEffect {

}