package com.jie.wealthmate.base

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.TimeSource

/**
 * MVI (Model-View-Intent) 패턴의 Container를 구현한 클래스입니다.
 * UI 상태 관리와 SideEffect의 중복 방지 및 순차 처리를 담당합니다.
 *
 * @param S UI 상태 타입
 * @property initialState 초기 상태
 * @property duplicateThresholdMs 동일한 SideEffect를 무시할 시간 간격 (기본 500ms)
 */
class RealContainer<S>(
    initialState: S,
    private val duplicateThresholdMs: Long = 500L,
) : Container<S> {

    // 컨테이너 생성 시점을 기준으로 시간 측정
    private val startTime = TimeSource.Monotonic.markNow()

    // 컨테이너 내부 비동기 작업을 위한 독립적 스코프
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // SideEffect 중복 방지를 위한 기록 관리
    private val recentSideEffects = mutableMapOf<UiSideEffect, Long>()
    private val sideEffectMutex = Mutex()

    private val isClosed = atomic(false)

    // SideEffect 이벤트를 안전하게 버퍼링하고 순차 처리하기 위한 채널
    private val sideEffectChannel = Channel<UiSideEffect>(capacity = Channel.UNLIMITED)

    // 내부 상태 및 SideEffect 흐름 제어
    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<S> = _uiState

    // replay=1로 설정하여 화면 전환 등의 상황에서 최신 이펙트 유실 방지
    private val _uiSideEffect = MutableSharedFlow<UiSideEffect>(
        replay = 1,
        extraBufferCapacity = 64
    )
    override val uiSideEffect: SharedFlow<UiSideEffect> = _uiSideEffect

    // 이벤트 처리를 위한 재사용 가능한 컨텍스트
    private val pluginContext = ContainerContext(
        initState = { _uiState.value },
        postSideEffect = { sideEffect -> sideEffectChannel.trySend(sideEffect) },
        reduceState = { reducer -> _uiState.update(reducer) }
    )

    init {
        startSideEffectProcessor()
    }

    private fun startSideEffectProcessor() {
        scope.launch {
            sideEffectChannel
                .receiveAsFlow()
                .catch { /* SideEffect 처리 중 예외 발생 시 로그 또는 처리 로직 추가 가능 */ }
                .collect { sideEffect ->
                    processSideEffect(sideEffect)
                }
        }
    }

    private suspend fun processSideEffect(sideEffect: UiSideEffect) {
        val currentTime = getCurrentTimeMillis()

        val shouldEmit = sideEffectMutex.withLock {
            val lastEmitTime = recentSideEffects[sideEffect]

            // 로딩 상태 변경은 항상 발행, 그 외는 시간 간격 체크
            val isAllowed = when (sideEffect) {
                is BaseUiSideEffect.ShowLoading -> true
                else -> lastEmitTime == null || (currentTime - lastEmitTime) >= duplicateThresholdMs
            }

            if (isAllowed) {
                recentSideEffects[sideEffect] = currentTime
                // 성능 관리를 위해 맵이 커지면 오래된 데이터 정리
                if (recentSideEffects.size > 30) {
                    cleanupOldEffects(currentTime)
                }
            }
            isAllowed
        }

        if (shouldEmit && scope.isActive) {
            try {
                _uiSideEffect.emit(sideEffect)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // emit 실패 시 전체 프로세스가 중단되지 않도록 예외 캡처
            }
        }
    }

    private fun cleanupOldEffects(currentTime: Long) {
        // 10초 이상 지난 기록은 중복 방지 의미가 없으므로 제거
        recentSideEffects.entries.removeAll { (currentTime - it.value) > 10000L }
    }

    private fun getCurrentTimeMillis(): Long {
        // Monotonic TimeSource를 사용하여 시스템 시간 변경에 무관하게 정확한 시간 차이 측정
        return startTime.elapsedNow().inWholeMilliseconds
    }

    override fun event(intent: ContainerContext<S>.() -> Unit) {
        if (isClosed.value.not()) {
            pluginContext.intent()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun clearSideEffects() {
        _uiSideEffect.resetReplayCache()
    }

    /**
     * 컨테이너를 종료하고 모든 비동기 작업을 중단합니다.
     */
    fun close() {
        if (isClosed.compareAndSet(expect = false, update = true)) {
            sideEffectChannel.close()
            scope.cancel()
        }
    }
}
