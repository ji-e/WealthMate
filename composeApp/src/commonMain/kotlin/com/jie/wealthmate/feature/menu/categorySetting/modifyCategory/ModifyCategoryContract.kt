package com.jie.wealthmate.feature.menu.categorySetting.modifyCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryTagVo

data class ModifyCategoryUiState(
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.INCOME,
    val categoryIcon: CategoryIconEnum = CategoryIconEnum.defaultCategoryIcon,
    val label: TextFieldValue = TextFieldValue(""),
    val tagLabel: TextFieldValue = TextFieldValue(""),
    val tagLabelItems: List<CategoryTagVo> = emptyList(),
    val modifyTagLabel: CategoryTagVo? = null,
    val isFixed: Boolean = false,
    val sort: Long = 0,
    val isChangedData: Boolean = false,
) : BaseUiState

sealed class ModifyCategoryUiSideEffect : UiSideEffect {
    data object OnSuccess : ModifyCategoryUiSideEffect()
    data object OnSuccessModifyTagLabel : ModifyCategoryUiSideEffect()
}
