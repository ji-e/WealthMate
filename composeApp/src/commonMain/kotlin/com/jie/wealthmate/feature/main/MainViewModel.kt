package com.jie.wealthmate.feature.main

import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.component.bottomNav.BottomNavItem

class MainViewModel : BaseViewModel<MainUiState>() {

    override val initialState: MainUiState = MainUiState()

    fun onTabSelected(item: BottomNavItem) {
        reduceState { state ->
            state.copy(selectedItem = item)
        }
    }
}
