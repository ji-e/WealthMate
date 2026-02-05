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
        getUserName()
    }

    private fun getUserName() {
        launchSafe(
            block = {
                authRepository.getUserName()
            }
        ) {
            reduceState { state ->
                state.copy(
                    userName = it.default()
                )
            }
        }
    }

    fun getToken(authCode: String?, email: String) {
        println(authCode)
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