package com.jie.wealthmate.feature.main

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.component.bottomNav.BottomNavItem

data class MainUiState(
    val selectedItem: BottomNavItem = BottomNavItem.Home,
    val previousItem: BottomNavItem = BottomNavItem.Home,
    val partnerUid: String = "",
    val partnerName: String = "",
) : BaseUiState
