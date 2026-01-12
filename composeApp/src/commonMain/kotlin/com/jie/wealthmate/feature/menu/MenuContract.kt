package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiActionEvent
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItem

data class MenuUiState(
    val menuEnums: List<MenuItem> = emptyList(),
) : BaseUiState

sealed interface MenuUiActionEvent : UiActionEvent {
    data class OnCLickMenu(val menu: MenuEnum) : MenuUiActionEvent
}