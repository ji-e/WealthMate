package com.jie.wealthmate.feature.budget.addBudget

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

class AddBudgetScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<AddBudgetUiState>() {

    override val initialState: AddBudgetUiState
        get() = AddBudgetUiState()

    init {
        getCategories(LargeCategoryEnum.INCOME)
        getCategories(LargeCategoryEnum.SAVING)
        getCategories(LargeCategoryEnum.EXPENSES)
    }

    fun updateIsCategoryTagInclude(isCategoryTagInclude: Boolean) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                isCategoryTagInclude = isCategoryTagInclude
            )
        }
    }

    fun updateIncomeTextField(id: String, value: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                incomeCategoryTextFieldMap = state.incomeCategoryTextFieldMap.toMutableMap().apply {
                    put(id, value)
                }.toImmutableMap()
            )
        }
    }

    fun updateExpensesTextField(id: String, value: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                expensesCategoryTextFieldMap = state.expensesCategoryTextFieldMap.toMutableMap().apply {
                    put(id, value)
                }.toImmutableMap()
            )
        }
    }

    fun updateSavingTextField(id: String, value: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                savingCategoryTextFieldMap = state.savingCategoryTextFieldMap.toMutableMap().apply {
                    put(id, value)
                }.toImmutableMap()
            )
        }
    }

    fun saveBudget() {
        // todo
    }

    private fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                val categoryItems = response.map { it.mapperToVo() }.toImmutableList()
                val textFieldMap = mutableMapOf<String, TextFieldValue>()

                categoryItems.forEach { category ->
                    textFieldMap[category.id] = TextFieldValue()
                    category.tags.forEach { categoryTagVo ->
                        if (categoryTagVo.id.isNullOrEmpty().not()) {
                            textFieldMap[categoryTagVo.id] = TextFieldValue()
                        }
                    }
                }

                when (largeCategoryEnum) {
                    LargeCategoryEnum.INCOME -> reduceState { state ->
                        state.copy(
                            incomeCategoryItems = categoryItems,
                            incomeCategoryTextFieldMap = textFieldMap.toImmutableMap()
                        )
                    }

                    LargeCategoryEnum.EXPENSES -> reduceState { state ->
                        state.copy(
                            expensesCategoryItems = categoryItems,
                            expensesCategoryTextFieldMap = textFieldMap.toImmutableMap()
                        )
                    }

                    else -> reduceState { state ->
                        state.copy(
                            savingCategoryItems = categoryItems,
                            savingCategoryTextFieldMap = textFieldMap.toImmutableMap()
                        )
                    }
                }
            }
    }
}
