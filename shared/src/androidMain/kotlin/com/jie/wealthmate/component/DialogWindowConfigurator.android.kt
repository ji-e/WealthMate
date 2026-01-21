package com.jie.wealthmate.component

import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat


actual class DialogWindowConfigurator {
    private var view: View? = null
    private var window: android.view.Window? = null

    fun setWindow(view: View) {
        this.view = view
        window = (view.parent as? DialogWindowProvider)?.window
    }

    actual fun configureWindow(
        dimAmount: Float,
        touchable: Boolean,
        focusable: Boolean,
    ) {
        window?.apply {
            // 1. 뒷배경 흐림(Dim) 플래그를 아예 제거
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            // 2. 시스템 바 배경을 그리는 플래그 설정 및 투명화
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
            navigationBarColor = android.graphics.Color.TRANSPARENT

            // API 30 이상에서 네비게이션 바의 미세한 구분선까지 투명하게
            navigationBarDividerColor = android.graphics.Color.TRANSPARENT

            // 3. 윈도우 자체 배경을 투명하게 (흰색 바 제거의 핵심)
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

            // 4. Edge-to-Edge 적용
            WindowCompat.setDecorFitsSystemWindows(this, false)

            // 5. 터치 및 포커스 설정
            if (!touchable) addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            else clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            if (!focusable) {
                addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            } else {
                clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

                val controller = WindowCompat.getInsetsController(this, decorView)
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }

            // 6. 전체 화면 크기 강제
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

    actual fun resetWindow() {
        window?.apply {
            setDimAmount(0f)
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
    }
}

@Composable
actual fun TransparentInteractiveDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        )
    ) {
        val view = LocalView.current
        val configurator = remember { DialogWindowConfigurator() }

        DisposableEffect(view) {
            configurator.setWindow(view)
            configurator.configureWindow(
                dimAmount = 0f,
                touchable = false,
                focusable = false
            )

            onDispose {
                configurator.resetWindow()
            }
        }

        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            content()
        }
    }
}