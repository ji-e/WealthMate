package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPoint
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectContainsPoint
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIEvent
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowLevelAlert
import platform.UIKit.UIWindowScene

// 1. 터치 영역을 필터링하는 Window
class PassthroughWindow @OptIn(ExperimentalForeignApi::class) constructor(frame: kotlinx.cinterop.CValue<CGRect>) :
    UIWindow(frame) {
    // Compose에서 콘텐츠의 영역을 이곳에 업데이트
    @OptIn(ExperimentalForeignApi::class)
    var contentRect: kotlinx.cinterop.CValue<CGRect>? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun hitTest(point: kotlinx.cinterop.CValue<CGPoint>, withEvent: UIEvent?): UIView? {
        val rect = contentRect

        // 1. 콘텐츠 영역이 설정되어 있고, 터치 좌표가 그 영역 '밖'이라면?
        if (rect != null && !CGRectContainsPoint(rect, point)) {
            return null // 이 윈도우는 터치를 무시하고 뒤쪽 윈도우로 넘김 (Pass-through)
        }

        // 2. 그 외(콘텐츠 안쪽)라면 정상적으로 Compose가 터치를 처리
        val hitView = super.hitTest(point, withEvent)
        return if (hitView == rootViewController?.view) null else hitView
    }
}

actual class DialogWindowConfigurator {
    actual fun configureWindow(
        dimAmount: Float,
        touchable: Boolean,
        focusable: Boolean,
    ) {
    }

    actual fun resetWindow() {}
}

@OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun TransparentInteractiveDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val currentContent by rememberUpdatedState(content)
    val density = LocalDensity.current.density // 픽셀 -> 포인트 변환용

    val window = remember {
        PassthroughWindow(frame = UIScreen.mainScreen.bounds).apply {
            // iOS 17 대응: Scene 연결
            val activeScene = UIApplication.sharedApplication.connectedScenes
                .mapNotNull { it as? UIWindowScene }
                .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
                ?: UIApplication.sharedApplication.keyWindow?.windowScene

            if (activeScene != null) {
                windowScene = activeScene
            }

            windowLevel = UIWindowLevelAlert + 10.0
            backgroundColor = UIColor.clearColor
            userInteractionEnabled = true
        }
    }

    DisposableEffect(window) {
        val controller = ComposeUIViewController(
            configure = { opaque = false }
        ) {
            // 2. 전체 화면을 덮는 Box (레이아웃 잡기용)
            Box(
                modifier = Modifier.background(Color.Transparent).fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // 3. 실제 콘텐츠를 감싸는 Box (영역 계산용)
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            // Compose 좌표(px)를 iOS 좌표(pt)로 변환
                            val bounds = coordinates.boundsInWindow()
                            val scale = density

                            // iOS Window에 "이 영역만 터치 받아라"고 전달
                            window.contentRect = CGRectMake(
                                x = bounds.left / scale.toDouble(),
                                y = bounds.top / scale.toDouble(),
                                width = bounds.width / scale.toDouble(),
                                height = bounds.height / scale.toDouble()
                            )
                        }
                ) {
                    currentContent()
                }
            }
        }

        controller.view.backgroundColor = UIColor.clearColor
        window.rootViewController = controller
        window.hidden = false

        onDispose {
            window.hidden = true
            window.windowScene = null
            window.rootViewController = null
            window.contentRect = null
        }
    }
}