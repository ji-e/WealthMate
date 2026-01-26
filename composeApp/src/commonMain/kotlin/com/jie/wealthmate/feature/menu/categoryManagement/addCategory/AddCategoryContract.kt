package com.jie.wealthmate.feature.menu.categoryManagement.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryTagVo

data class AddCategoryUiState(
    val isDataChanged: Boolean = false,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.INCOME,
    val categoryIcon: CategoryIconEnum = CategoryIconEnum.defaultCategoryIcon,
    val label: TextFieldValue = TextFieldValue(""),
    val tagLabel: TextFieldValue = TextFieldValue(""),
    val tagLabelItems: List<CategoryTagVo> = emptyList(),
    val isFixed: Boolean = false,
) : BaseUiState

sealed class AddCategoryUiSideEffect : UiSideEffect {
    data object OnSuccessSave : AddCategoryUiSideEffect()
}
