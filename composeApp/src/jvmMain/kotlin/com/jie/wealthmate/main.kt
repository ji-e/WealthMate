package com.jie.wealthmate

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "WealthMate",
    ) {
        App()
    }
}