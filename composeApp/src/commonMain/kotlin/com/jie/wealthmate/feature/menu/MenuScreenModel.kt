package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItemData
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.utils.default
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

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

    fun updateUser(accessToken: String?, email: String) {
        authRepository.saveAuthData(accessToken.default(), null, email)

        reduceState { state ->
            state.copy(
                userName = email
            )
        }
    }

    fun logout() {
        launchSafe(
            block = {
                GoogleAuthProvider.create(
                    credentials = GoogleAuthCredentials(serverId = "808791516955-mvuausum2tbonst3bf4kqna8t99tkkk6.apps.googleusercontent.com")
                ).signOut()
                authRepository.clearAuthData()
            }
        ){
            showSnackbar("계정 연동이 해제 되었습니다.")

            reduceState { state ->
                state.copy(
                    userName = ""
                )
            }
        }
    }

}