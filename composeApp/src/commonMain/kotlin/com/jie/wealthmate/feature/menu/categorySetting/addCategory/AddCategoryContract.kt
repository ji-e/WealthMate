package com.jie.wealthmate.feature.menu.categorySetting.addCategory

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.component.CategoryIconEnum

data class AddCategoryUiState(
    val categoryIcon: CategoryIconEnum = CategoryIconEnum.defaultCategoryIcon,
    val label: TextFieldValue = TextFieldValue(""),
) : BaseUiState
