package com.jie.wealthmate

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.component.topbar.TopBarItem

data class MainUiState(
    val title: TopBarItem.Title? = null,
    val readingItem: TopBarItem.ReadingItem? = null,
    val trailingItem: List<TopBarItem.TrailingItem>? = null,
) : BaseUiState

