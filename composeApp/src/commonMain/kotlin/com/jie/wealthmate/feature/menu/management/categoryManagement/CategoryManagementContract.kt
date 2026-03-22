package com.jie.wealthmate.feature.menu.management.categoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum

data class CategoryManagementUiState(
    val currentLargeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES
) : BaseUiState
