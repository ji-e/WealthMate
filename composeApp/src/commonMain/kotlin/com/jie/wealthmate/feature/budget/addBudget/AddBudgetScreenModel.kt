package com.jie.wealthmate.feature.budget.addBudget

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.datetime.LocalDate

class AddBudgetScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<AddBudgetUiState>() {

    override val initialState: AddBudgetUiState
        get() = AddBudgetUiState()

    init {
        LargeCategoryEnum.entries.forEach { getCategories(it) }
    }

    fun updateSelectedMonth(month: LocalDate = today) {
        reduceState { state ->
            if (state.selectedMonth == month) return@reduceState state
            state.copy(selectedMonth = month)
        }
    }

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        reduceState { state ->
            if (state.selectedLargeCategory == largeCategory) return@reduceState state
            state.copy(selectedLargeCategory = largeCategory)
        }
    }

    fun updateIsCategoryTagInclude(isCategoryTagInclude: Boolean) {
        reduceState { state ->
            if (state.isCategoryTagInclude == isCategoryTagInclude) return@reduceState state

            val updateMap = { items: List<CategoryVo>, currentMap: Map<String, TextFieldValue> ->
                currentMap.toMutableMap().apply {
                    items.forEach { category ->
                        if (category.tags.isNotEmpty()) {
                            if (isCategoryTagInclude) {
                                val sum = category.tags.sumOf { tag ->
                                    this[tag.id]?.text?.toLongOrNull() ?: 0L
                                }
                                put(category.id, TextFieldValue(sum.toString()))
                            } else {
                                val currentTotal = this[category.id]?.text?.toLongOrNull() ?: 0L
                                distributeProportionally(category, currentTotal, this)
                            }
                        }
                    }
                }.toImmutableMap()
            }

            val newState = state.copy(
                isDataChanged = true,
                isCategoryTagInclude = isCategoryTagInclude,
                incomeCategoryTextFieldMap = updateMap(state.incomeCategoryItems, state.incomeCategoryTextFieldMap),
                expensesCategoryTextFieldMap = updateMap(state.expensesCategoryItems, state.expensesCategoryTextFieldMap),
                savingCategoryTextFieldMap = updateMap(state.savingCategoryItems, state.savingCategoryTextFieldMap)
            )
            newState.copy(remainBudget = calculateRemainBudget(newState))
        }
    }

    fun updateIncomeTextField(id: String, value: TextFieldValue) =
        updateCategoryTextField(LargeCategoryEnum.INCOME, id, value)

    fun updateExpensesTextField(id: String, value: TextFieldValue) =
        updateCategoryTextField(LargeCategoryEnum.EXPENSES, id, value)

    fun updateSavingTextField(id: String, value: TextFieldValue) =
        updateCategoryTextField(LargeCategoryEnum.SAVING, id, value)

    private fun updateCategoryTextField(categoryType: LargeCategoryEnum, id: String, value: TextFieldValue) {
        reduceState { state ->
            val currentMap = when (categoryType) {
                LargeCategoryEnum.INCOME -> state.incomeCategoryTextFieldMap
                LargeCategoryEnum.EXPENSES -> state.expensesCategoryTextFieldMap
                LargeCategoryEnum.SAVING -> state.savingCategoryTextFieldMap
            }

            if (currentMap[id] == value) return@reduceState state

            val items = when (categoryType) {
                LargeCategoryEnum.INCOME -> state.incomeCategoryItems
                LargeCategoryEnum.EXPENSES -> state.expensesCategoryItems
                LargeCategoryEnum.SAVING -> state.savingCategoryItems
            }

            val newMap = currentMap.toMutableMap().apply {
                put(id, value)
                val newAmount = value.text.toLongOrNull() ?: 0L

                if (state.isCategoryTagInclude) {
                    items.find { cat -> cat.tags.any { it.id == id } }?.let { category ->
                        val sum = category.tags.sumOf { tag ->
                            (if (tag.id == id) value.text else (this[tag.id]?.text ?: "0")).toLongOrNull() ?: 0L
                        }
                        put(category.id, TextFieldValue(sum.toString()))
                    }
                } else {
                    items.find { it.id == id }?.let { category ->
                        if (category.tags.isNotEmpty()) {
                            distributeProportionally(category, newAmount, this)
                        }
                    }
                }
            }.toImmutableMap()

            val newState = when (categoryType) {
                LargeCategoryEnum.INCOME -> state.copy(incomeCategoryTextFieldMap = newMap)
                LargeCategoryEnum.EXPENSES -> state.copy(expensesCategoryTextFieldMap = newMap)
                LargeCategoryEnum.SAVING -> state.copy(savingCategoryTextFieldMap = newMap)
            }.copy(isDataChanged = true)

            newState.copy(remainBudget = calculateRemainBudget(newState))
        }
    }

    private fun distributeProportionally(category: CategoryVo, newTotal: Long, map: MutableMap<String, TextFieldValue>) {
        if (category.tags.isEmpty()) return
        val oldTotal = category.tags.sumOf { map[it.id]?.text?.toLongOrNull() ?: 0L }

        if (oldTotal > 0) {
            val ratio = newTotal.toDouble() / oldTotal
            var currentSum = 0L
            category.tags.forEachIndexed { index, tag ->
                val tagId = tag.id ?: return@forEachIndexed
                val newAmount = if (index == category.tags.lastIndex) newTotal - currentSum
                else ((map[tagId]?.text?.toLongOrNull() ?: 0L) * ratio).toLong()
                currentSum += newAmount
                map[tagId] = TextFieldValue(newAmount.toString())
            }
        } else {
            val evenAmount = newTotal / category.tags.size
            var currentSum = 0L
            category.tags.forEachIndexed { index, tag ->
                val tagId = tag.id ?: return@forEachIndexed
                val newAmount = if (index == category.tags.lastIndex) newTotal - currentSum else evenAmount
                currentSum += newAmount
                map[tagId] = TextFieldValue(newAmount.toString())
            }
        }
    }

    private fun calculateRemainBudget(state: AddBudgetUiState): Long {
        val expensesSum = calculateCategorySum(state.expensesCategoryItems, state.expensesCategoryTextFieldMap, state.isCategoryTagInclude)
        val savingSum = calculateCategorySum(state.savingCategoryItems, state.savingCategoryTextFieldMap, state.isCategoryTagInclude)
        return state.totalBudget - expensesSum - savingSum
    }

    private fun calculateCategorySum(items: List<CategoryVo>, textFieldMap: Map<String, TextFieldValue>, isCategoryTagInclude: Boolean): Long {
        return items.sumOf { category ->
            if (isCategoryTagInclude && category.tags.isNotEmpty()) {
                category.tags.sumOf { textFieldMap[it.id]?.text?.toLongOrNull() ?: 0L }
            } else {
                textFieldMap[category.id]?.text?.toLongOrNull() ?: 0L
            }
        }
    }

    private fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name).apiFlow { response ->
            val categoryItems = response.map { it.mapperToVo() }.map { category ->
                val newTags = category.tags.map { tag ->
                    if (tag.id.isNullOrEmpty()) tag.copy(id = "${category.id}_${tag.label}") else tag
                }.toImmutableList()
                category.copy(tags = newTags)
            }.toImmutableList()

            val textFieldMap = mutableMapOf<String, TextFieldValue>()
            categoryItems.forEach { category ->
                textFieldMap[category.id] = TextFieldValue()
                category.tags.forEach { tag -> tag.id?.let { textFieldMap[it] = TextFieldValue() } }
            }

            reduceState { state ->
                val newState = when (largeCategoryEnum) {
                    LargeCategoryEnum.INCOME -> state.copy(incomeCategoryItems = categoryItems, incomeCategoryTextFieldMap = textFieldMap.toImmutableMap())
                    LargeCategoryEnum.EXPENSES -> state.copy(expensesCategoryItems = categoryItems, expensesCategoryTextFieldMap = textFieldMap.toImmutableMap())
                    LargeCategoryEnum.SAVING -> state.copy(savingCategoryItems = categoryItems, savingCategoryTextFieldMap = textFieldMap.toImmutableMap())
                }
                if (largeCategoryEnum != LargeCategoryEnum.INCOME) newState.copy(remainBudget = calculateRemainBudget(newState)) else newState
            }
        }
    }

    fun saveBudget() {
        // todo
    }
}
