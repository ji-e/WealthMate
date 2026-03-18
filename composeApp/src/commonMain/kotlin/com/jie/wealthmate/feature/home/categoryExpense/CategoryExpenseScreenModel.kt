package com.jie.wealthmate.feature.home.categoryExpense

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow

class CategoryExpenseScreenModel(
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    initialStatusType: StatusType,
    initialLargeCategory: LargeCategoryEnum,
) : BaseScreenModel<CategoryExpenseUiState>() {

    override val initialState: CategoryExpenseUiState = CategoryExpenseUiState(
        statusType = initialStatusType
    )

    private val filterFlow = MutableStateFlow(initialStatusType to initialLargeCategory)


    fun updateStatusType(statusType: StatusType) {
        filterFlow.value = statusType to filterFlow.value.second
    }

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        filterFlow.value = filterFlow.value.first to largeCategory
    }
}
