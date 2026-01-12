package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.component.MenuItem

class MenuScreenModel : BaseScreenModel<MenuUiState>() {

    override val initialState: MenuUiState
        get() = MenuUiState(
            menuEnums = MenuItem.menuItems
        )
}