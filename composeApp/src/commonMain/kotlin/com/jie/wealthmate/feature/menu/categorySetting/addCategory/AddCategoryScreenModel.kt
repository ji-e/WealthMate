package com.jie.wealthmate.feature.menu.categorySetting.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel

class AddCategoryScreenModel : BaseScreenModel<AddCategoryUiState>() {

    override val initialState: AddCategoryUiState
        get() = AddCategoryUiState()

    fun updateCategoryLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                label = textFieldValue
            )
        }
    }
}