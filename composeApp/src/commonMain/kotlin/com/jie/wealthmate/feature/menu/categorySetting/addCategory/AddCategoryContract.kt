package com.jie.wealthmate.feature.menu.categorySetting.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.vo.CategoryTagVo

data class AddCategoryUiState(
    val categoryIcon: CategoryIconEnum = CategoryIconEnum.defaultCategoryIcon,
    val label: TextFieldValue = TextFieldValue(""),
    val tagLabel: TextFieldValue = TextFieldValue(""),
    val tagLabelItems: List<CategoryTagVo> = emptyList(),
) : BaseUiState
