package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItemData

data class IncomeCategorySettingUiState(
    val initialized: Boolean = false,
    val incomeCategoryItems: List<CategoryItemData> = emptyList(),
) : BaseUiState
