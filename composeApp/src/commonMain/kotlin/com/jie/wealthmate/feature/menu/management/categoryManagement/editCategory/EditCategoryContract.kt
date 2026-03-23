package com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryTagVo

data class EditCategoryUiState(
    val isDataChanged: Boolean = false,
    val categoryId: String? = null,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.INCOME,
    val categoryIcon: CategoryIconEnum = CategoryIconEnum.defaultCategoryIcon,
    val label: TextFieldValue = TextFieldValue(),
    val tagLabel: TextFieldValue = TextFieldValue(),
    val tagLabelItems: List<CategoryTagVo> = emptyList(),
    val sort: Long = 0,
    val isFixed: Boolean = false,
) : BaseUiState

sealed class EditCategoryUiSideEffect : UiSideEffect {
    data object OnSuccess : EditCategoryUiSideEffect()
    data object OnSuccessModifyTagLabel : EditCategoryUiSideEffect()
}
