package com.jie.wealthmate.feature.menu.categorySetting.categorySetting

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItemData

data class CategorySettingUiState(
    val isInitialized: Boolean = false,
    val incomeCategoryItems: List<CategoryItemData> = emptyList(),
) : BaseUiState
