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
    var accessToken: String? = null

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

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(serverId = "1001016412934-av4h457eq1vtastir4hjdomf1bnd11hp.apps.googleusercontent.com")
            )

            GoogleButtonUiContainer(
                onGoogleSignInResult = { googleUser ->
                    println(googleUser)
                    // 드디어 여기서 유저 정보를 받습니다!
                    val token = googleUser?.accessToken
                    screenModel.getToken(googleUser?.serverAuthCode)
                    if (token != null) {
                        accessToken = token
                        screenModel.updateToken(token)
                        println("성공! GDA용 액세스 토큰: $token")
                    }
                },
                scopes = listOf("https://www.googleapis.com/auth/drive.appdata"),
            ) {
                WMButton(
                    text = "Google로 로그인",
                    onClick = { this.onClick() } // UiContainerScope의 onClick 호출
                )
            }
            WMButton(
                text = "sync",
                onClick = { screenModel.onSyncClick() }
            )

            WMButton(
                text = "sync2",
                onClick = { screenModel.onSyncClick2() }
            )

        }
    }


}