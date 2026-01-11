package com.jie.wealthmate.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface Container<S> {
    val uiState: StateFlow<S>
    val uiSideEffect: SharedFlow<UiSideEffect>

    fun event(intent: ContainerContext<S>.() -> Unit)
    fun clearSideEffects()
}

interface ContainerHost<S> {
    val container: Container<S>

    fun resetSideEffect()
}

class ContainerContext<S>(
    val initState: () -> S,
    val postSideEffect: suspend (UiSideEffect) -> Unit,
    val reduceState: ((S) -> S) -> Unit,
) {
    val state: S
        get() = initState()

}

fun <S> ContainerHost<S>.event(
    transformer: ContainerContext<S>.() -> Unit,
) {
    container.event {
        transformer()
    }
}

@Composable
fun <STATE : UiState> ContainerHost<STATE>.collectSideEffect(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    clearResource: () -> Unit = {},
    sideEffect: suspend (UiSideEffect) -> Unit,
) {
    collectSideEffectInternal(
        isReady = true,
        lifecycleState = lifecycleState,
        clearResource = clearResource,
        sideEffect = sideEffect
    )
}


@Composable
private fun <STATE : UiState> ContainerHost<STATE>.collectSideEffectInternal(
    isReady: Boolean = false,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    clearResource: () -> Unit = {},
    sideEffect: suspend (UiSideEffect) -> Unit,
) {
    val sideEffectFlow = container.uiSideEffect
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(isReady, sideEffectFlow, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(lifecycleState) {
            sideEffectFlow.collect {
                if (isReady) {
                    sideEffect(it)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY) {
                clearResource()
                try {
                    container.clearSideEffects() // replay 버퍼 초기화
                } catch (e: Exception) {
                    Unit
                }
                resetSideEffect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            clearResource()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun <STATE : UiState> ContainerHost<STATE>.collectAsState(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
): State<STATE> {
    val stateFlow = container.uiState
    val lifecycleOwner = LocalLifecycleOwner.current

    val stateFlowLifecycleAware = remember(stateFlow, lifecycleOwner) {
        stateFlow.flowWithLifecycle(lifecycleOwner.lifecycle, lifecycleState)
    }

    val initialValue = stateFlow.value
    return stateFlowLifecycleAware.collectAsState(initialValue)
}

@Composable
fun <T : BaseUiState> State<T>.onLoading(
    block: @Composable () -> Unit,
): State<T> {
    if (value is BaseUiState.Loading) {
        block()
    }
    return this
}

@Composable
inline fun <S : UiState, reified SS : BaseUiState.Success<SS>> State<S>.onSuccess(
    block: @Composable (data: SS) -> Unit,
): State<S> {
    val currentValue = value

    if (currentValue is BaseUiState.Success<*> && currentValue is SS) {
        block(currentValue)
    }
    return this
}

@Composable
fun <T : BaseUiState> State<T>.onError(
    block: @Composable (message: String) -> Unit,
): State<T> {
    (value as? BaseUiState.Error)?.let { block(it.message) }
    return this
}

