package com.jie.wealthmate.component

import androidx.compose.runtime.Composable

actual class DialogWindowConfigurator actual constructor() {
    actual fun configureWindow(
        dimAmount: Float,
        touchable: Boolean,
        focusable: Boolean,
    ) {
    }

    actual fun resetWindow() {
    }
}

@Composable
actual fun TransparentInteractiveDialog(
    onDismissRequest: () -> Unit,
    content: @Composable (() -> Unit),
) {
}