package com.jie.wealthmate.base

import cafe.adriel.voyager.core.model.ScreenModel
import com.jie.wealthmate.MainUiManager
import com.jie.wealthmate.component.topbar.TopBarItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MVI 패턴을 적용한 화면의 기본 클래스입니다.
 * Voyager의 ScreenModel을 상속받아 화면 전환을 관리하고, ContainerHost를 구현하여 UI 상태와 이벤트를 처리합니다.
 *
 * @param S 이 화면에서 사용할 UI 상태(UiState)의 타입
 */
abstract class BaseScreenModel<S : UiState> : ScreenModel, ContainerHost<S> {

    /**
     * KMP 환경에 맞는 CoroutineScope를 생성합니다.
     * - Dispatchers.Main: UI 관련 작업을 처리하기 위한 디스패처입니다. (KMP는 플랫폼별 기본 UI 스레드에 맞게 제공)
     * - SupervisorJob: 자식 코루틴 중 하나에서 예외가 발생해도 다른 코루틴이나 부모 스코프에 영향을 주지 않아,
     *   UI의 안정성을 높입니다.
     */
    protected val screenScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * 화면의 초기 UI 상태를 정의합니다.
     */
    abstract val initialState: S

    /**
     * MVI 컨테이너의 인스턴스입니다.
     * lazy 초기화를 통해 실제로 컨테이너가 필요한 시점에 생성됩니다.
     */
    override val container: Container<S> by lazy {
        RealContainer(initialState = initialState)
    }

    /**
     * UI 상태를 업데이트하는 함수입니다.
     * @param uiState 현재 상태를 받아 새로운 상태를 반환하는 람다 함수
     */
    fun reduceState(
        uiState: (uiState: S) -> S,
    ) = event {
        reduceState { currentState -> uiState(currentState) }
    }

    /**
     * 일회성 이벤트(SideEffect)를 발생시키는 함수입니다.
     * @param postFunc 현재 상태를 기반으로 SideEffect를 생성하거나 Unit을 반환하는 suspend 람다 함수
     */
    protected fun <SEU> postSideEffect(
        postFunc: suspend (state: S) -> SEU,
    ) = event {
        screenScope.launch {
            val sideEffectOrUnit = postFunc(state)
            if (sideEffectOrUnit is UiSideEffect) {
                postSideEffect(sideEffectOrUnit)
            }
        }
    }

    /**
     * SideEffect를 초기 상태로 리셋합니다.
     */
    override fun resetSideEffect() = postSideEffect {
        BaseUiSideEffect.Idle
    }

    override fun onDispose() {
        // RealContainer 리소스 정리
        try {
            when (container) {
                is RealContainer -> (container as RealContainer<S>).close()
                else -> println("Container doesn't support close operation: ${container::class.simpleName}")
            }
        } catch (e: Exception) {
            println("Error closing container: $e")
        }
    }

    /**
     * 전역 TopBar를 업데이트합니다.
     */
    fun updateTopBar(
        title: TopBarItem.Title? = null,
        readingItem: TopBarItem.ReadingItem? = null,
        trailingItem: List<TopBarItem.TrailingItem>? = null,
    ) {
        MainUiManager.updateTopBar(
            title = title,
            readingItem = readingItem,
            trailingItem = trailingItem
        )
    }

    /**
     * Snackbar 메시지를 표시하는 SideEffect를 발생시킵니다.
     * @param message 표시할 메시지 문자열
     */
    fun showSnackbar(
        message: String,
    ) = screenScope.launch {
        MainUiManager.emitSideEffect(BaseUiSideEffect.ShowSnackbar(message))
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
    fun hideKeyboard() = screenScope.launch {
        MainUiManager.emitSideEffect(BaseUiSideEffect.HideKeyboard)
    }

    /**
     * 예외 처리와 로딩 상태 관리를 포함하는 공통 코루틴 실행 함수입니다.
     *
     * @param T 실행할 비즈니스 로직의 반환 타입
     * @param showLoading 로딩 인디케이터 표시 여부
     * @param errorMsg 에러 발생 시 표시할 기본 메시지
     * @param onError 에러 발생 시 추가로 실행할 작업
     * @param onSuccess 성공 시 실행할 작업 (block의 결과값을 인자로 받음)
     * @param block 실행할 메인 비즈니스 로직
     */
    protected fun <T> launchSafe(
        block: suspend () -> T,
        showLoading: Boolean = true,
        errorMsg: String? = null,
        onError: (suspend (Throwable) -> Unit)? = null,
        onSuccess: (suspend (T) -> Unit)? = null,
    ) = event {
        screenScope.launch {
            hideKeyboard()
            delay(timeMillis = 100)

            if (showLoading) {
                postSideEffect { 
                    BaseUiSideEffect.ShowLoading(true)
                }
            }
            try {
                val result = block()
                onSuccess?.invoke(result)
            } catch (e: Exception) {
                if (e is CancellationException) throw e

                val finalMessage = errorMsg ?: e.message ?: "오류가 발생했습니다."
                showSnackbar(message = finalMessage)
                onError?.invoke(e)
            } finally {
                if (showLoading) {
                    postSideEffect {
                        BaseUiSideEffect.ShowLoading(false)
                    }
                }
            }
        }
    }
}
