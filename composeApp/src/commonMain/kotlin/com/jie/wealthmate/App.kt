package com.jie.wealthmate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.jie.wealthmate.feature.main.MainScreen
import com.jie.wealthmate.theme.WMTheme


@Composable
@Preview
fun App() {
    WMTheme() {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}
