package com.jie.wealthmate.base

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * Screen -> ui
 * ui 상태 전달
 * */
interface UiState

@Stable
interface BaseUiState : UiState {
    /**
     * 최초 진입 시 Loading 상태에서 UI 분기 처리가 필요한 경우
     * */
    @Immutable
    data object Loading : BaseUiState

    /**
     * API 호출 성공 시 UI 표시에 필요한 데이터 세팅
     * */
    @Immutable
    interface Success<S : UiState> : BaseUiState

    /**
     * Error Case 별 UI 분기 처리가 필요한 경우
     * */
    @Immutable
    data class Error(
        val message: String,
    ) : BaseUiState
}

/**
 * Screen -> ui
 * 네트워크 통신 등 작업 이후 ui에서 처리가 필요한 작업이 있을 때
 * */
interface UiSideEffect

@Stable
sealed class BaseUiSideEffect : UiSideEffect {
    @Immutable
    data object Idle : UiSideEffect

    @Immutable
    data class ShowLoading(val isShowLoading: Boolean) : UiSideEffect

    @Immutable
    data class ShowSnackbar(
        val message: String,
        val triggerBack: Boolean = false,
    ) : UiSideEffect

    @Immutable
    data class ShowSnackbarWithAction(
        val message: String,
        val actionText: String,
        val action: () -> Unit,
    ) : UiSideEffect

    @Immutable
    data object HideKeyboard : UiSideEffect

}

/**
 * ui -> Screen or business logic
 * 모든 사용자 동작 관리
 * */
interface UiActionEvent

@Stable
sealed class BaseUiActionEvent : UiActionEvent {

}