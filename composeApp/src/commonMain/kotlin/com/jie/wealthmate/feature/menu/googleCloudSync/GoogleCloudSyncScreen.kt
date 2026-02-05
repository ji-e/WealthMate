@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.googleCloudSync

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer

class GoogleCloudSyncScreen() : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: GoogleCloudSyncScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()


        LaunchedEffect(Unit) {
            screenModel.updateTopBar(
                title = TopBarItem.Title(MenuEnum.GOOGLE_SYNC.label),
                readingItem = TopBarItem.ReadingItem().copy(
                    action = { navigator.pop() }
                ),
            )
        }

        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(serverId = "1001016412934-av4h457eq1vtastir4hjdomf1bnd11hp.apps.googleusercontent.com")
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            GoogleButtonUiContainer(
                onGoogleSignInResult = { googleUser ->
                    println("googleUser::: ${googleUser?.serverAuthCode}")
                    screenModel.getToken(googleUser?.serverAuthCode)
                },
                scopes = listOf("https://www.googleapis.com/auth/drive.file"),
            ) {
                WMButton(
                    text = "Google로 로그인",
                    onClick = { this.onClick() }
                )
            }
            WMButton(
                text = "upload",
                onClick = { screenModel.upload() }
            )

            WMButton(
                text = "download",
                onClick = { screenModel.download() }
            )
        }
    }
}