package com.jie.wealthmate.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * MVI 컨테이너의 핵심 인터페이스입니다.
 * UI 상태(StateFlow)와 일회성 이벤트(SharedFlow)를 외부에 노출합니다.
 *
 * @param S UI 상태의 타입
 */
interface Container<S> {
    /**
     * 화면의 UI 상태를 나타내는 StateFlow입니다.
     * Compose에서는 collectAsState()와 함께 사용하여 상태 변화에 따라 UI를 자동으로 업데이트할 수 있습니다.
     */
    val uiState: StateFlow<S>

    /**
     * Toast 메시지 표시, 화면 이동 등 일회성 이벤트를 전달하는 SharedFlow입니다.
     * SideEffect는 상태와 달리 소비되면 사라져야 하는 이벤트를 처리하는 데 사용됩니다.
     */
    val uiSideEffect: SharedFlow<UiSideEffect>

    /**
     * 사용자 입력이나 시스템 이벤트(Intent)를 처리하는 함수입니다.
     * 이 함수를 통해 UI 상태를 변경하거나 SideEffect를 발생시킬 수 있습니다.
     * @param intent 이벤트를 처리할 로직을 담은 람다 함수
     */
    fun event(intent: ContainerContext<S>.() -> Unit)

    /**
     * SharedFlow의 Replay Cache를 비워, 이전 SideEffect가 새로운 구독자에게 전달되는 것을 방지합니다.
     * 화면이 사라질 때 호출하여 메모리 누수를 방지하고 원치 않는 동작을 막을 수 있습니다.
     */
    fun clearSideEffects()
}

/**
 * Container를 소유하는 호스트(ViewModel, ScreenModel 등)가 구현하는 인터페이스입니다.
 * @param S UI 상태의 타입
 */
interface ContainerHost<S> {
    val container: Container<S>

    /**
     * SideEffect를 초기 상태(보통 Idle)로 리셋합니다.
     */
    fun resetSideEffect()
}

/**
 * 이벤트 처리의 컨텍스트를 제공하는 클래스입니다.
 * 이벤트 핸들러 내에서 현재 상태에 접근하고, 상태를 변경하며, SideEffect를 발생시킬 수 있습니다.
 *
 * @param S UI 상태의 타입
 * @property initState 현재 UI 상태를 가져오는 함수
 * @property postSideEffect SideEffect를 발생시키는 suspend 함수
 * @property reduceState UI 상태를 변경하는 함수
 */
class ContainerContext<S>(
    val initState: () -> S,
    val postSideEffect: suspend (UiSideEffect) -> Unit,
    val reduceState: ((S) -> S) -> Unit,
) {
    /**
     * 현재 UI 상태(state)에 직접 접근할 수 있는 프로퍼티입니다.
     */
    val state: S
        get() = initState()
}

/**
 * ContainerHost의 확장 함수로, 이벤트 처리를 간결하게 작성할 수 있도록 돕습니다.
 * @param transformer 이벤트 처리 로직
 */
fun <S> ContainerHost<S>.event(
    transformer: ContainerContext<S>.() -> Unit,
) {
    container.event {
        transformer()
    }
}

/**
 * KMP 환경에 맞게 수정된 Composable 함수입니다.
 * 화면(Screen)의 생명주기에 맞춰 SideEffect를 수집하고 처리합니다.
 *
 * @param STATE UI 상태의 타입
 * @param sideEffect 수신된 SideEffect를 처리할 suspend 람다 함수
 */
@Composable
fun <STATE : UiState> ContainerHost<STATE>.collectSideEffect(
    sideEffect: suspend (UiSideEffect) -> Unit,
) {
    val sideEffectFlow = container.uiSideEffect
    // 콜백이 갱신되어도 코루틴 내에서 최신 값을 참조할 수 있도록 함
    val currentSideEffect by rememberUpdatedState(sideEffect)

    LaunchedEffect(sideEffectFlow) {
        sideEffectFlow.collect {
            currentSideEffect(it)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // replay 버퍼를 초기화하여 이전 이펙트가 다시 발생하는 것을 방지
            container.clearSideEffects()
            // SideEffect 상태를 초기값으로 리셋
            resetSideEffect()
        }
    }
}

/**
 * Container의 uiState를 Compose의 State로 변환합니다.
 * @param STATE UI 상태의 타입
 * @return Compose에서 관찰 가능한 State 객체
 */
@Composable
fun <STATE : UiState> ContainerHost<STATE>.collectAsState(): State<STATE> {
    return container.uiState.collectAsState()
}

/**
 * UI 상태가 '로딩' 상태일 때 주어진 Composable 블록을 실행합니다.
 */
@Composable
fun <T : BaseUiState> State<T>.onLoading(
    block: @Composable () -> Unit,
): State<T> {
    if (value is BaseUiState.Loading) {
        block()
    }
    return this
}

/**
 * UI 상태가 특정 '성공' 상태일 때 주어진 Composable 블록을 실행합니다.
 */
@Composable
inline fun <S : UiState, reified SS : BaseUiState.Success<*>> State<S>.onSuccess(
    block: @Composable (data: SS) -> Unit,
): State<S> {
    val currentValue = value
    if (currentValue is SS) {
        block(currentValue)
    }
    return this
}

/**
 * UI 상태가 '에러' 상태일 때 주어진 Composable 블록을 실행합니다.
 */
@Composable
fun <T : BaseUiState> State<T>.onError(
    block: @Composable (message: String, throwable: Throwable?) -> Unit,
): State<T> {
    (value as? BaseUiState.Error)?.let {
        block(it.message, it.throwable)
    }
    return this
}
