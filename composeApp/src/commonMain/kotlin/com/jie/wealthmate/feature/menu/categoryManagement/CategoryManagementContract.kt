package com.jie.wealthmate.feature.menu.categoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData

data class CategoryManagementUiState(
    val isInitialized: Boolean = false,
    val incomeCategoryItems: List<CategoryItemData> = emptyList(),
) : BaseUiState
