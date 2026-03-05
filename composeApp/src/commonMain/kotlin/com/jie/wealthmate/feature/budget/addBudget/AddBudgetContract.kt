package com.jie.wealthmate.feature.budget.addBudget

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.datetime.LocalDate

data class AddBudgetUiState(
    val isDataChanged: Boolean = false,
    val selectedMonth: LocalDate = today,
    val selectedLargeCategory: LargeCategoryEnum = LargeCategoryEnum.INCOME,
    val isCategoryTagInclude: Boolean = false,
    val remainBudget: Long = 5000000L,
    val incomeCategoryItems: ImmutableList<CategoryVo> = persistentListOf(),
    val expensesCategoryItems: ImmutableList<CategoryVo> = persistentListOf(),
    val savingCategoryItems: ImmutableList<CategoryVo> = persistentListOf(),
    val incomeCategoryTextFieldMap: ImmutableMap<String, TextFieldValue> = persistentMapOf(),
    val expensesCategoryTextFieldMap: ImmutableMap<String, TextFieldValue> = persistentMapOf(),
    val savingCategoryTextFieldMap: ImmutableMap<String, TextFieldValue> = persistentMapOf(),
) : BaseUiState {
    val totalBudget: Long
        get() {
            return incomeCategoryItems.sumOf { category ->
                if (isCategoryTagInclude && category.tags.isNotEmpty()) {
                    category.tags.sumOf { tag ->
                        incomeCategoryTextFieldMap[tag.id]?.text?.toLongOrNull().default()
                    }
                } else {
                    incomeCategoryTextFieldMap[category.id]?.text?.toLongOrNull().default()
                }
            }
        }
}

sealed class AddBudgetUiSideEffect : UiSideEffect {
    data object OnSuccess : AddBudgetUiSideEffect()
}

