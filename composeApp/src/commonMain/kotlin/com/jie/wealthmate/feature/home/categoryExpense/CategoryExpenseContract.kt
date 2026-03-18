package com.jie.wealthmate.feature.home.categoryExpense

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum

data class CategoryExpenseUiState(
    val statusType: StatusType = StatusType.MONTH,
    val largeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
) : BaseUiState