package com.jie.wealthmate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.jie.wealthmate.feature.main.MainScreen
import com.jie.wealthmate.feature.menu.data.googleCloudSync.GoogleCloudSyncScreenModel
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.theme.WMTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.koin.compose.koinInject

object TokenManager {
    var accessToken: String? = null // 구글 드라이브 API용 토큰이 담길 곳
}

@Composable
@Preview
fun App() {
    val authRepository: AuthRepository = koinInject()
    val googleCloudSyncScreenModel = koinInject<GoogleCloudSyncScreenModel>()

    GoogleAuthProvider.create(
        credentials = GoogleAuthCredentials(serverId = "808791516955-mvuausum2tbonst3bf4kqna8t99tkkk6.apps.googleusercontent.com")
    )
//    GoogleButtonUiContainer(
//        onGoogleSignInResult = { googleUser ->
//            println("googleUser::: $googleUser")
//            googleCloudSyncScreenModel.syncFromCloudOnStart()
//        },
//        scopes = listOf(
//            "https://www.googleapis.com/auth/drive.appdata",
//            "https://www.googleapis.com/auth/drive.file",
//            "https://www.googleapis.com/auth/drive.metadata.readonly" // 메타데이터 읽기
//        ),
//    ) {
//        if (authRepository.isLoggedIn()) {
//            this.onClick()
//        }
//    }

    WMTheme() {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}
