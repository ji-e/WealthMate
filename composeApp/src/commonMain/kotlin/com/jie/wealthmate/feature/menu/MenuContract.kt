package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItem

data class MenuUiState(
    val menuEnums: List<MenuItem> = emptyList(),
) : BaseUiState

sealed class MenuUiSideEffect : UiSideEffect {
    data class OnCLickMenu(val menu: MenuEnum) : MenuUiSideEffect()
}