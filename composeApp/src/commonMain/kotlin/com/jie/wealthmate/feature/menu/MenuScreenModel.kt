package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseScreenModel

class MenuScreenModel : BaseScreenModel<MenuUiState>() {

    override val initialState: MenuUiState
        get() = MenuUiState()
}