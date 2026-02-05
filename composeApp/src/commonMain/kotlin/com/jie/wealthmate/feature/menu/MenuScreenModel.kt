package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItemData
import com.jie.wealthmate.repository.AuthRepository

class MenuScreenModel(
    val authRepository: AuthRepository,
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
                    userName = it
                )
            }
        }
    }

}