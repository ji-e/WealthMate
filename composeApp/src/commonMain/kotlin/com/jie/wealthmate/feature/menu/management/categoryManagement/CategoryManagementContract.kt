package com.jie.wealthmate.feature.menu.management.categoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.vo.CategoryVo

data class CategoryManagementUiState(
    val categoryItems: List<CategoryVo> = emptyList(),
) : BaseUiState
