package com.jie.wealthmate.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * MVI 패턴을 적용한 ViewModel의 기본 클래스입니다.
 * Android Lifecycle의 ViewModel을 상속받으며, ContainerHost를 통해 UI 상태 및 이벤트를 관리합니다.
 *
 * @param S 이 ViewModel에서 관리할 UI 상태(UiState)의 타입
 */
abstract class BaseViewModel<S : UiState> : ViewModel(), ContainerHost<S> {

    /**
     * 화면의 초기 UI 상태입니다. 상속받는 클래스에서 구현해야 합니다.
     */
    abstract val initialState: S

    /**
     * 비동기 작업(네트워크 통신, DB 접근 등)을 위한 별도의 CoroutineScope입니다.
     */
    protected val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * UI 상태와 SideEffect를 처리하는 MVI 컨테이너입니다.
     */
    override val container: Container<S> by lazy {
        RealContainer(initialState = initialState)
    }

    /**
     * UI 상태를 업데이트합니다.
     * @param reducer 현재 상태를 기반으로 새로운 상태를 반환하는 람다
     */
    protected fun reduceState(reducer: (S) -> S) = event {
        reduceState { currentState -> reducer(currentState) }
    }

    /**
     * 일회성 이벤트(SideEffect)를 발생시킵니다.
     * @param sideEffect 발생시킬 SideEffect
     */
    protected fun postSideEffect(sideEffect: UiSideEffect) = event {
        viewModelScope.launch {
            postSideEffect(sideEffect)
        }
    }

    /**
     * 현재 상태를 기반으로 일회성 이벤트(SideEffect)를 발생시킵니다.
     * @param postFunc 현재 상태를 기반으로 SideEffect를 생성하거나 Unit을 반환하는 suspend 람다
     */
    protected fun <SEU> postSideEffect(postFunc: suspend (state: S) -> SEU) = event {
        viewModelScope.launch {
            val result = postFunc(state)
            if (result is UiSideEffect) {
                postSideEffect(result)
            }
        }
    }

    override fun resetSideEffect() = postSideEffect { BaseUiSideEffect.Idle }

    /**
     * ViewModel 종료 시 리소스를 정리합니다.
     */
    override fun onCleared() {
        super.onCleared()
        (container as? RealContainer<S>)?.close()
        ioScope.cancel()
    }

    // region --- 공통 UI 제어 유틸리티 (자체 Container 사용) ---

    /**
     * 로딩 인디케이터를 표시하거나 숨기는 SideEffect를 발생시킵니다.
     */
    fun showLoading(isShow: Boolean) = postSideEffect(BaseUiSideEffect.ShowLoading(isShow))

    /**
     * Snackbar 메시지를 표시하는 SideEffect를 발생시킵니다.
     */
    fun showSnackbar(message: String) = postSideEffect(BaseUiSideEffect.ShowSnackbar(message))

    /**
     * 액션 버튼이 포함된 Snackbar 메시지를 표시하는 SideEffect를 발생시킵니다.
     */
    fun showSnackbarWithAction(
        message: String,
        actionLabel: String,
        onAction: () -> Unit,
    ) = postSideEffect(
        BaseUiSideEffect.ShowSnackbarWithAction(
            message = message,
            actionLabel = actionLabel,
            onAction = onAction
        )
    )

    /**
     * 소프트 키보드를 숨기는 SideEffect를 발생시킵니다.
     */
    fun hideKeyboard() = postSideEffect(BaseUiSideEffect.HideKeyboard)

    // endregion


    // region --- 안전한 코루틴 실행 및 API 처리 ---


    // endregion
}
