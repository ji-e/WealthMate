package com.jie.wealthmate.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
fun calculateAdjustedToastPadding(customPadding: Int): Dp {
    val density = LocalDensity.current

    // 전체 safeDrawing 영역 (IME + navigation 등)
    val safeBottomPx = WindowInsets.safeDrawing.getBottom(density)

    // navigation bar 영역 - scaffold 내부에서 자동으로 적용되기 때문.
    val navigationBottomPx = WindowInsets.navigationBars.getBottom(density)

    // navigation 영역 제외한 padding (예: 키보드 올라왔을 때만 값 있음)
    val adjustedPx = (safeBottomPx - navigationBottomPx).coerceAtLeast(0)

    return with(density) { adjustedPx.toDp() }.plus(customPadding.dp)
}
