package com.jie.wealthmate.component

import androidx.compose.runtime.Composable

expect class DialogWindowConfigurator() {
    fun configureWindow(
        dimAmount: Float = 0f,
        touchable: Boolean = false,
        focusable: Boolean = false
    )

    fun resetWindow()
}

@Composable
expect fun TransparentInteractiveDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
)