package com.jie.wealthmate.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// 스낵바 데이터 클래스
data class SnackbarData(
    val message: String,
    val actionLabel: String? = null,
    val duration: Long = 3000L,
    val onAction: (() -> Unit)? = null,
)

// 스낵바 상태 관리
class SnackbarState {
    private val _currentSnackbar = mutableStateOf<SnackbarData?>(null)
    val currentSnackbar: State<SnackbarData?> = _currentSnackbar

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        duration: Long = 3000L,
        onAction: (() -> Unit)? = null,
    ) {
        _currentSnackbar.value = SnackbarData(message, actionLabel, duration, onAction)
        delay(duration)
        _currentSnackbar.value = null
    }

    fun dismiss() {
        _currentSnackbar.value = null
    }
}

@Composable
fun rememberSnackbarState(): SnackbarState {
    return remember { SnackbarState() }
}

// 커스텀 스낵바 컴포저블
@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF323232)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = snackbarData.message,
                color = Color.White,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            snackbarData.actionLabel?.let { label ->
                TextButton(
                    onClick = {
                        snackbarData.onAction?.invoke()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = label,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

// 스낵바 호스트
@Composable
fun CustomSnackbarHost(
    snackbarState: SnackbarState,
    modifier: Modifier = Modifier,
) {
    val currentSnackbar by snackbarState.currentSnackbar

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = currentSnackbar != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            currentSnackbar?.let { data ->
                CustomSnackbar(
                    snackbarData = data,
                    onDismiss = { snackbarState.dismiss() }
                )
            }
        }
    }
}

class SnackbarController(private val snackbarHostState: SnackbarHostState) {
    suspend fun showMessage(
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ): SnackbarResult {
        return snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
    }
}

@Composable
fun rememberSnackbarController(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): SnackbarController {
    return remember(snackbarHostState) {
        SnackbarController(snackbarHostState)
    }
}


@Composable
fun calculateAdjustedToastPadding(customPadding: Int = 0): Dp {
    val density = LocalDensity.current

    // 전체 safeDrawing 영역 (IME + navigation 등)
    val safeBottomPx = WindowInsets.safeDrawing.getBottom(density)

    // navigation bar 영역 - scaffold 내부에서 자동으로 적용되기 때문.
    val navigationBottomPx = WindowInsets.navigationBars.getBottom(density)

    val systemBarsPx = WindowInsets.systemBars.getBottom(density)

    // navigation 영역 제외한 padding (예: 키보드 올라왔을 때만 값 있음)
    val adjustedPx = (safeBottomPx - navigationBottomPx + systemBarsPx).coerceAtLeast(0)

    return with(density) { adjustedPx.toDp() }.plus(customPadding.dp)
}
