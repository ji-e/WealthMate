package com.jie.wealthmate.base

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.jie.wealthmate.component.SnackbarController
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.rememberSnackbarController

abstract class BaseScreen : Screen {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }
        val snackbarController = rememberSnackbarController(snackbarHostState)

        ScreenContent(
            snackbarController = snackbarController,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = calculateAdjustedToastPadding(20))
                )
            }
        )
    }

    @Composable
    abstract fun ScreenContent(
        snackbarController: SnackbarController,
        snackbarHost: @Composable () -> Unit
    )

}

