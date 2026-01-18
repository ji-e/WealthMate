package com.jie.wealthmate

import com.jie.wealthmate.base.BaseScreenModel

open class MainScreenModel() : BaseScreenModel<MainUiState>() {

    override val initialState: MainUiState
        get() = MainUiState()

}