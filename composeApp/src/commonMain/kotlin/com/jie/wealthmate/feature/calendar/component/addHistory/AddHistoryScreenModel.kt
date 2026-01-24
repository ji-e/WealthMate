package com.jie.wealthmate.feature.calendar.component.addHistory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel

class AddHistoryScreenModel() : BaseScreenModel<AddHistoryUiState>() {

    override val initialState: AddHistoryUiState
        get() = AddHistoryUiState()

    fun updateAmount(amount: TextFieldValue) {
        reduceState { state ->
            state.copy(
                amount = amount
            )
        }
    }

    fun updateContent(content: TextFieldValue) {
        reduceState { state ->
            state.copy(
                content = content
            )
        }
    }
}