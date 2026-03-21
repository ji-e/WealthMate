@file:OptIn(ExperimentalComposeUiApi::class)

package com.jie.wealthmate.base

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.LoadingOverlay
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

/**
 * 모든 화면의 공통 로직(로딩, 스낵바, 키보드 제어 등)을 처리하는 BaseScreen 입니다.
 *
 * @param S UI 상태 타입
 * @param viewModel 해당 화면의 ViewModel
 * @param onBack 뒤로가기 처리가 필요한 경우 호출되는 콜백
 * @param onSideEffect 추가적인 SideEffect 처리가 필요한 경우 호출되는 콜백
 * @param content 화면에 표시할 실제 UI 컨텐츠
 */
@Composable
fun <S : UiState> BaseScreen(
    viewModel: BaseViewModel<S>,
    onBack: (() -> Unit)? = null,
    onSideEffect: (suspend (UiSideEffect) -> Unit)? = null,
    content: @Composable (state: S) -> Unit,
) {
    val uiState by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var isShowLoading by remember { mutableStateOf(false) }

    // 콜백의 최신 상태를 유지하여 SideEffect 수집 루프에서 stale capture 방지
    val currentOnBack by rememberUpdatedState(onBack)
    val currentOnSideEffect by rememberUpdatedState(onSideEffect)

    // 시스템 뒤로가기 버튼 처리
    BackHandlerWrapper(enabled = currentOnBack != null) {
        Napier.e("backback")
        currentOnBack?.invoke()
    }

    // SideEffect 처리 통합
    viewModel.collectSideEffect { sideEffect ->
        // 공통 SideEffect 처리
        when (sideEffect) {
            is BaseUiSideEffect.ShowLoading -> {
                isShowLoading = sideEffect.isShowLoading
            }

            is BaseUiSideEffect.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.showSnackbar(message = sideEffect.message)
                }
                if (sideEffect.triggerBack) {
                    currentOnBack?.invoke()
                }
            }

            is BaseUiSideEffect.ShowSnackbarWithAction -> {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = sideEffect.message,
                        actionLabel = sideEffect.actionLabel
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        sideEffect.onAction()
                    }
                }
            }

            is BaseUiSideEffect.HideKeyboard -> {
                focusManager.clearFocus()
            }

            else -> Unit
        }

        // 추가 SideEffect 처리 (AuthSideEffect 등)
        currentOnSideEffect?.invoke(sideEffect)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // 배경 클릭 시 포커스 해제 (자식의 클릭 이벤트를 방해하지 않음)
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        // 실제 화면 컨텐츠
        // navigationBarsPadding은 각 화면에서 개별적으로 처리하도록 BaseScreen에서는 제거하거나 신중히 결정
        // 여기서는 기본적으로 하단 바가 있는 앱 구조라면 content 내부에서 관리하는 것이 유연함
        content(uiState)

        // 스낵바 위치 및 패딩 조정
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding() // 스낵바는 시스템 바 위에 떠야 함
                .padding(horizontal = 24.dp)
                .padding(bottom = 80.dp) // 하단 탭바 등과의 겹침 방지 조정
        )

        // 로딩 레이어 (최상단)
        if (isShowLoading) {
            LoadingOverlay()
        }
    }
}
