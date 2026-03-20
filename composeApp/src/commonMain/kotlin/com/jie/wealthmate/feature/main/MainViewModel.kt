package com.jie.wealthmate.feature.main

import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.component.bottomNav.BottomNavItem

class MainViewModel : BaseViewModel<MainUiState>() {

    override val initialState: MainUiState = MainUiState()

    fun onTabSelected(item: BottomNavItem) {
        reduceState { state ->
            if (state.selectedItem == item) return@reduceState state
            state.copy(
                previousItem = state.selectedItem,
                selectedItem = item
            )
        }
    }

    fun navigateBackToPreviousTab() {
        reduceState { state ->
            state.copy(
                selectedItem = state.previousItem
            )
        }
    }
}
