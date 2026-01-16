package com.jie.wealthmate

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.base.BaseUiSideEffect
import com.jie.wealthmate.component.topbar.TopBarItem

open class MainScreenModel() : BaseScreenModel<MainUiState>() {

    override val initialState: MainUiState
        get() = MainUiState()

    fun updateTopBar(
        title: TopBarItem.Title? = null,
        readingItem: TopBarItem.ReadingItem? = null,
        trailingItem: List<TopBarItem.TrailingItem>? = null,
    ) {
        println(title)
        reduceState { state ->
            state.copy(
                title = title,
                readingItem = readingItem,
                trailingItem = trailingItem
            )
        }
    }

    /**
     * Snackbar 메시지를 표시하는 SideEffect를 발생시킵니다.
     * @param message 표시할 메시지 문자열
     */
    fun showSnackbar(
        message: String,
    ) {
        postSideEffect {
            BaseUiSideEffect.ShowSnackbar(message = message)
        }
    }

    /**
     * 액션 버튼이 포함된 Snackbar 메시지를 표시하는 SideEffect를 발생시킵니다.
     * @param message 표시할 메시지 문자열
     * @param actionText 액션 버튼에 표시될 텍스트
     * @param action 버튼 클릭 시 실행될 람다 함수
     */
    fun showSnackbarWithAction(
        message: String,
        actionText: String,
        action: () -> Unit,
    ) = postSideEffect {
        BaseUiSideEffect.ShowSnackbarWithAction(
            message = message,
            actionText = actionText,
            action = action
        )
    }

    /**
     * 키보드를 숨기는 SideEffect를 발생시킵니다.
     */
    fun hideKeyboard() = postSideEffect {
        BaseUiSideEffect.HideKeyboard
    }

}