package com.jie.wealthmate.feature.menu.management.categoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.CategoryItemData

data class CategoryManagementUiState(
    val categoryItems: List<CategoryItemData> = emptyList(),
) : BaseUiState
