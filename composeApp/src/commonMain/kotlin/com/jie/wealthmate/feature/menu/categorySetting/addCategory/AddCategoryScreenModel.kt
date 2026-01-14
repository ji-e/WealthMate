package com.jie.wealthmate.feature.menu.categorySetting.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.vo.CategoryTagVo

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

    fun updateCategoryTagLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                tagLabel = textFieldValue
            )
        }
    }

    fun addCategoryTabLabel() {
        reduceState { state ->
            val categoryTag = CategoryTagVo(label = state.tagLabel.text)
            val isExisted = state.tagLabelItems.any { it.label == categoryTag.label }

            if (categoryTag.label.isEmpty()) {
                showSnackbar("상세 태그 이름을 입력해 주세요.")
                return@reduceState state
            }
            if (isExisted) {
                showSnackbar("이미 존재하는 태그 입니다.")
                state
            } else {
                state.copy(
                    tagLabel = TextFieldValue(""),
                    tagLabelItems = state.tagLabelItems.toMutableList().apply { add(categoryTag) }
                )
            }
        }
    }
}