package com.jie.wealthmate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.jie.wealthmate.feature.menu.googleCloudSync.GoogleCloudSyncScreenModel
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.theme.WMTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    GoogleAuthProvider.create(
        credentials = GoogleAuthCredentials(serverId = "1001016412934-av4h457eq1vtastir4hjdomf1bnd11hp.apps.googleusercontent.com")
    )

    val authRepository: AuthRepository = koinInject()
    val googleCloudSyncScreenModel = koinInject<GoogleCloudSyncScreenModel>()

    Napier.e  ("token::: ${authRepository.getAccessToken()}")
    if (authRepository.isLoggedIn()) {
        googleCloudSyncScreenModel.syncFromCloudOnStart()
    }

    WMTheme() {
        Navigator(MainScreen())
    }
}
