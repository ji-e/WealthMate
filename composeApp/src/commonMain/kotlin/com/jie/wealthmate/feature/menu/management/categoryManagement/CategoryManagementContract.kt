package com.jie.wealthmate.feature.menu.management.categoryManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryVo

data class CategoryManagementUiState(
    val categoryMap: Map<LargeCategoryEnum, List<CategoryVo>> = emptyMap(),
    val currentLargeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES
) : BaseUiState {
    val currentCategoryItems: List<CategoryVo>?
        get() = categoryMap[currentLargeCategory]
}
