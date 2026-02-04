package com.jie.wealthmate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.jie.wealthmate.feature.menu.googleCloudSync.GoogleCloudSyncScreenModel
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.theme.WMTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val authRepository: AuthRepository = koinInject()
    val googleCloudSyncScreenModel = koinInject<GoogleCloudSyncScreenModel>()


    if (authRepository.isLoggedIn()) {
        googleCloudSyncScreenModel.syncFromCloudOnStart()
    }

    WMTheme() {
        Navigator(MainScreen())
    }
}
