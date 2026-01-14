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

    fun addCategoryTabLabel(tagLabel: TextFieldValue?) {
        reduceState { state ->
            if (tagLabel == null) {
                state.copy(
                    tagLabel = TextFieldValue(""),
                )
                return@reduceState state
            }

            if (tagLabel.text.isEmpty()) {
                showSnackbar("상세 태그 이름을 입력해 주세요.")
                return@reduceState state
            }

            val categoryTag = CategoryTagVo(label = tagLabel.text)
            val isExisted = state.tagLabelItems.any { it.label == categoryTag.label }

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