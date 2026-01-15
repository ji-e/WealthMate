package com.jie.wealthmate.feature.menu.categorySetting.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryTagVo

data class AddCategoryUiState(
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.INCOME,
    val categoryIcon: CategoryIconEnum = CategoryIconEnum.defaultCategoryIcon,
    val label: TextFieldValue = TextFieldValue(""),
    val tagLabel: TextFieldValue = TextFieldValue(""),
    val tagLabelItems: List<CategoryTagVo> = emptyList(),
) : BaseUiState

sealed class AddCategoryUiSideEffect : UiSideEffect {
    data object OnSuccessSave : AddCategoryUiSideEffect()
}
