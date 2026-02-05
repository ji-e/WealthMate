package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItemData
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.utils.default

class MenuScreenModel(
    val authRepository: AuthRepository,
    val googleRepository: GoogleRepository,
) : BaseScreenModel<MenuUiState>() {

    override val initialState: MenuUiState
        get() = MenuUiState(
            menuEnums = MenuItemData.menuItems
        )

    fun onMenuClick(menu: MenuEnum) {
        postSideEffect {
            MenuUiSideEffect.OnCLickMenu(menu)
        }
    }

    init {
        if (authRepository.isLoggedIn()) {
            getUserName()
        }
    }

    private fun getUserName() {
        reduceState { state ->
            state.copy(
                userName = authRepository.getUserName().default()
            )
        }
    }

    fun getToken(authCode: String?, email: String) {
        authCode ?: return

        launchSafe(
            block = {
                googleRepository.fetchGoogleAuth(
                    authCode = authCode,
                    email = email
                )
            }
        ) {}
    }

}