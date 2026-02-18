package com.jie.wealthmate.feature.budget.addBudget

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

data class AddBudgetUiState(
    val isDataChanged: Boolean = false,
    val isCategoryTagInclude: Boolean = false,
    val incomeCategoryItems: ImmutableList<CategoryVo> = persistentListOf(),
    val expensesCategoryItems: ImmutableList<CategoryVo> = persistentListOf(),
    val savingCategoryItems: ImmutableList<CategoryVo> = persistentListOf(),
    val incomeCategoryTextFieldMap: ImmutableMap<String, TextFieldValue> = persistentMapOf(),
    val expensesCategoryTextFieldMap: ImmutableMap<String, TextFieldValue> = persistentMapOf(),
    val savingCategoryTextFieldMap: ImmutableMap<String, TextFieldValue> = persistentMapOf(),
) : BaseUiState
