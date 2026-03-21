package com.jie.wealthmate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.jie.wealthmate.feature.main.MainScreen
import com.jie.wealthmate.theme.WMTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider


@Composable
@Preview
fun App() {
    LaunchedEffect(Unit) {
        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(serverId = BuildKonfig.GOOGLE_WEB_CLIENT_ID)
        )
    }
    WMTheme() {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}
