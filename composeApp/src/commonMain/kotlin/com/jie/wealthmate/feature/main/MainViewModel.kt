package com.jie.wealthmate.feature.main

import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.repository.AuthRepository
import io.github.aakira.napier.Napier

class MainViewModel(
    private val authRepository: AuthRepository,
    private val accountProvider: AccountProvider,
) : BaseViewModel<MainUiState>() {

    override val initialState: MainUiState = MainUiState()

    init {
        try {
            accountProvider.updateAccount(authRepository.getUserName())
        } catch (e: Exception) {
            Napier.e("Failed to initialize MainViewModel", e)
        }
    }

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
