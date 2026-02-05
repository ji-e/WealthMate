package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItemData

data class MenuUiState(
    val menuEnums: List<MenuItemData> = emptyList(),
    val userName: String = "",
) : BaseUiState

sealed class MenuUiSideEffect : UiSideEffect {
    data class OnCLickMenu(val menu: MenuEnum) : MenuUiSideEffect()
}