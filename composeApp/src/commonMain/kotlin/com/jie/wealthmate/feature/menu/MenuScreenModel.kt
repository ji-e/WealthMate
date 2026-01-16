package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.MainScreenModel
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItem

class MenuScreenModel(
    val mainScreenModel: MainScreenModel
) : BaseScreenModel<MenuUiState>() {

    override val initialState: MenuUiState
        get() = MenuUiState(
            menuEnums = MenuItem.menuItems
        )

    fun onMenuClick(menu: MenuEnum) {
        postSideEffect {
            MenuUiSideEffect.OnCLickMenu(menu)
        }
    }

}