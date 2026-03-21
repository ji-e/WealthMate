package com.jie.wealthmate.base

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandlerWrapper(enabled: Boolean = true, onBack: () -> Unit)
