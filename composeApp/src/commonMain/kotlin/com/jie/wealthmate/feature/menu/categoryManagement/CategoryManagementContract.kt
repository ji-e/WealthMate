package com.jie.wealthmate.feature.menu.categoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData

data class CategoryManagementUiState(
    val categoryItems: List<CategoryItemData> = emptyList(),
) : BaseUiState
