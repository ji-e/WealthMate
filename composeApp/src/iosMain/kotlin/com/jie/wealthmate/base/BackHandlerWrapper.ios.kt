package com.jie.wealthmate.base

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandlerWrapper(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have a hardware back button. 
    // Usually, back navigation is handled by the navigation controller or swipe gestures.
}
