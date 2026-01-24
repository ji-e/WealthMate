package com.jie.wealthmate

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.jie.wealthmate.theme.WMTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    WMTheme() {
        Navigator(MainScreen())
    }
}
