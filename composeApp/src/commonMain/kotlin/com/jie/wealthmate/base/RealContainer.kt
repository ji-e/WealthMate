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
import kotlin.time.Clock

/**
 * MVI (Model-View-Intent) 패턴의 Container를 구현한 클래스입니다.
 * 이 클래스는 UI 상태(State)와 일회성 이벤트(SideEffect)를 관리하며,
 * KMP (Kotlin Multiplatform) 환경에서 사용할 수 있도록 플랫폼 독립적으로 작성되었습니다.
 *
 * @param S Container가 관리할 UI 상태의 타입
 * @property initialState 초기 UI 상태
 */
class RealContainer<S>(
    initialState: S
) : Container<S> {

    // 비동기 작업 및 다른 SideEffect 처리 중 예외가 발생해도 전체 스코프에 영향을 주지 않도록 SupervisorJob 사용
    // IO Dispatcher를 사용하여 백그라운드 스레드에서 작업을 수행
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // SideEffect의 중복 발생을 추적하기 위한 Map.
    // KMP 환경의 동시성 문제를 해결하기 위해 Mutex로 보호됩니다.
    private val recentSideEffects = mutableMapOf<UiSideEffect, Long>()
    private val sideEffectMutex = Mutex()

    // 동일한 SideEffect를 무시할 시간 간격 (밀리초)
    private val duplicateThresholdMs = 500L

    // Container가 close 되었는지 여부를 나타내는 원자적 Boolean 변수
    // kotlinx.atomicfu 라이브러리를 사용하여 KMP 환경에서 동시성 보장
    private val isClosed = atomic(false)

    // SideEffect를 임시로 저장하고 중복을 제거하기 위한 채널
    // Channel.UNLIMITED를 사용하여 버퍼 크기에 제한을 두지 않음
    private val sideEffectChannel = Channel<UiSideEffect>(capacity = Channel.UNLIMITED)

    init {
        // 채널에서 이벤트를 받아 중복 체크 후 SharedFlow로 전달하는 코루틴 실행
        scope.launch {
            sideEffectChannel
                .receiveAsFlow()
                .catch { cause ->
                    // SideEffect 처리 플로우에서 오류 발생 시 로그 출력
                    println("SideEffect flow error: $cause")
                }.collect { sideEffect ->
                    val currentTime = Clock.System.now().toEpochMilliseconds()
                    val lastEmitTime: Long?

                    // Mutex를 사용하여 recentSideEffects에 안전하게 접근
                    sideEffectMutex.withLock {
                        lastEmitTime = recentSideEffects[sideEffect]
                    }

                    // SideEffect를 발행해야 하는지 여부를 결정
                    // - BaseUiSideEffect.ShowLoading 타입은 항상 발행
                    // - 그 외에는 마지막 발행 시간이 없거나, 설정된 임계값(duplicateThresholdMs)을 초과했을 때 발행
                    val shouldEmit = when (sideEffect) {
                        is BaseUiSideEffect.ShowLoading -> true
                        else -> lastEmitTime == null || (currentTime - lastEmitTime) >= duplicateThresholdMs
                    }

                    if (shouldEmit) {
                        println("새로운 SideEffect 발행: $sideEffect")

                        // emit 함수는 suspend 함수이며, UI에서 구독(collect)이 지연되면 다음 emit이 지연될 수 있습니다.
                        // 이를 방지하기 위해 Channel을 사용하여 이벤트를 버퍼링합니다.
                        try {
                            if (scope.isActive) {
                                internalUiSideEffect.emit(value = sideEffect)
                                // Mutex를 사용하여 recentSideEffects를 안전하게 업데이트
                                sideEffectMutex.withLock {
                                    recentSideEffects[sideEffect] = currentTime
                                }
                            }
                        } catch (e: CancellationException) {
                            // 코루틴 취소는 정상적인 동작이므로 다시 던짐
                            throw e
                        } catch (e: Exception) {
                            println("SideEffect 발행 실패: $e, sideEffect: $sideEffect")
                        }

                        // 오래된 SideEffect 기록 정리 (10초 이상 경과)
                        // 성능 저하를 방지하기 위해 주기적으로 맵을 정리합니다.
                        sideEffectMutex.withLock {
                            val iterator = recentSideEffects.entries.iterator()
                            while (iterator.hasNext()) {
                                val entry = iterator.next()
                                if ((currentTime - entry.value) > 10000L) {
                                    iterator.remove()
                                }
                            }
                        }
                    } else {
                        val timeSinceLastEmit = if (lastEmitTime != null) currentTime - lastEmitTime else 0
                        println("중복 SideEffect 무시됨 (${timeSinceLastEmit}ms 경과): $sideEffect")
                    }
                }
        }
    }

    // Container의 내부 로직을 처리하는 컨텍스트
    private val pluginContext: ContainerContext<S> = ContainerContext(
        initState = { uiState.value },
        postSideEffect = { sideEffect ->
            // UI 이벤트 발생 시 SideEffect를 채널로 전송
            // trySend는 non-suspending 함수로, 채널이 꽉 찼을 경우 실패할 수 있으나 UNLIMITED 버퍼이므로 항상 성공
            sideEffectChannel.trySend(element = sideEffect)
        },
        reduceState = { reducer ->
            // UI 상태 업데이트
            internalUiStateFlow.update(reducer)
        }
    )

    // 내부에서만 수정 가능한 MutableStateFlow
    private val internalUiStateFlow = MutableStateFlow(value = initialState)
    // 외부에 노출되는 읽기 전용 StateFlow
    override val uiState: StateFlow<S> = internalUiStateFlow

    // 내부에서만 수정 가능한 MutableSharedFlow
    // replay: 구독자가 연결되었을 때, 이전에 발행된 N개의 이벤트를 다시 전달받도록 설정
    // extraBufferCapacity: 버퍼 용량을 추가하여 이벤트가 빠르게 발생해도 유실되지 않도록 함
    private val internalUiSideEffect = MutableSharedFlow<UiSideEffect>(
        replay = 3,
        extraBufferCapacity = 64
    )
    // 외부에 노출되는 읽기 전용 SharedFlow
    override val uiSideEffect: SharedFlow<UiSideEffect> = internalUiSideEffect

    /**
     * UI 이벤트를 받아 처리합니다.
     * @param intent UI에서 발생한 이벤트를 처리하는 람다 함수
     */
    override fun event(intent: ContainerContext<S>.() -> Unit) {
        pluginContext.intent()
    }

    /**
     * SharedFlow의 replay 버퍼를 비웁니다.
     * 화면 전환이나 특정 상황에서 이전에 발행된 SideEffect를 더 이상 받지 않으려면 호출합니다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun clearSideEffects() {
        // SharedFlow의 replay 캐시를 초기화
        internalUiSideEffect.resetReplayCache()
    }

    /**
     * Container를 종료하고 모든 리소스를 해제합니다.
     * ViewModel 등이 파괴될 때 호출하여 메모리 누수를 방지합니다.
     */
    fun close() {
        // isClosed.compareAndSet(expected, new): 현재 값이 expected와 같으면 new 값으로 변경하고 true 반환
        if (isClosed.compareAndSet(expect = false, update = true).not()) {
            return
        }
        // 채널을 닫고, CoroutineScope를 취소하여 모든 코루틴을 종료
        runCatching {
            sideEffectChannel.close()
        }.onFailure { cause ->
            println("sideEffectChannel 종료 실패: $cause")
        }
        scope.cancel()
    }
}
