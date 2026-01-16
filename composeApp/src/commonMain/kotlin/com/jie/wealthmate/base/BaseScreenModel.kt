package com.jie.wealthmate.base

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    private val screenScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

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
}
