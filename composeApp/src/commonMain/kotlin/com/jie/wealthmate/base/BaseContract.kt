package com.jie.wealthmate.base

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * [UiState]
 * 화면의 모든 정보를 담는 상태 객체의 최상위 인터페이스입니다.
 * 모든 UI State 클래스는 이 인터페이스를 상속받아야 합니다.
 */
interface UiState

/**
 * [BaseUiState]
 * 앱 전반에서 공통적으로 사용되는 UI 상태 패턴을 정의합니다.
 * 주로 API 호출의 생명주기(Loading, Success, Error)를 관리할 때 사용됩니다.
 */
@Stable
interface BaseUiState : UiState {
    /**
     * 최초 진입 시 또는 데이터 로딩 중인 상태
     */
    @Immutable
    data object Loading : BaseUiState

    /**
     * 성공적으로 데이터를 로드한 상태를 나타내는 마커 인터페이스입니다.
     * 실제 화면의 상태 클래스가 이를 상속받아 구체적인 데이터를 포함합니다.
     */
    @Immutable
    interface Success<S : UiState> : BaseUiState

    /**
     * 작업 중 에러가 발생한 상태
     * @param message 사용자에게 노출할 에러 메시지
     * @param throwable 상세 분석을 위한 예외 객체 (선택 사항)
     */
    @Immutable
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : BaseUiState
}

/**
 * [UiSideEffect]
 * 토스트, 스낵바, 화면 이동 등 상태로 관리되지 않는 일회성 이벤트를 위한 인터페이스입니다.
 */
interface UiSideEffect

/**
 * [BaseUiSideEffect]
 * BaseScreen 수준에서 공통으로 처리되는 시스템 및 UI 이벤트를 정의합니다.
 */
@Stable
sealed class BaseUiSideEffect : UiSideEffect {
    /** 기본 상태 (이벤트 없음) */
    @Immutable
    data object Idle : BaseUiSideEffect()

    /** 전체 화면 로딩 인디케이터 표시 여부 */
    @Immutable
    data class ShowLoading(val isShowLoading: Boolean) : BaseUiSideEffect()

    /** 일반 메시지 스낵바 표시 */
    @Immutable
    data class ShowSnackbar(
        val message: String,
        val triggerBack: Boolean = false,
    ) : BaseUiSideEffect()

    /** 실행 버튼이 포함된 스낵바 표시 */
    @Immutable
    data class ShowSnackbarWithAction(
        val message: String,
        val actionLabel: String,
        val onAction: () -> Unit,
    ) : BaseUiSideEffect()

    /** 현재 포커스 해제 및 키보드 숨기기 */
    @Immutable
    data object HideKeyboard : BaseUiSideEffect()
}

/**
 * [UiActionEvent]
 * 버튼 클릭, 스와이프 등 사용자의 모든 행위(Intent)를 캡슐화하는 인터페이스입니다.
 */
interface UiActionEvent

/**
 * [BaseUiActionEvent]
 * 앱 내 여러 화면에서 공통으로 발생할 수 있는 액션을 정의합니다.
 */
@Stable
sealed class BaseUiActionEvent : UiActionEvent {
    // 향후 OnRetry, OnRefresh 등의 공통 액션을 여기에 추가할 수 있습니다.
}
