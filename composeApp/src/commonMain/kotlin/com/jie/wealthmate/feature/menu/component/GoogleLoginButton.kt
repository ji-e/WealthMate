package com.jie.wealthmate.feature.menu.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.google.GoogleUser
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.android_light_sq_na

@Composable
fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    onSuccessResult: (GoogleUser) -> Unit,
) {
    GoogleButtonUiContainer(
        onGoogleSignInResult = { googleUser ->
            googleUser?.let { onSuccessResult(it) }
        },
        scopes = listOf(
            "https://www.googleapis.com/auth/drive.appdata",
            "https://www.googleapis.com/auth/drive.file",
            "https://www.googleapis.com/auth/drive.metadata.readonly" // 메타데이터 읽기
        ),
    ) {
        Row(
            modifier = modifier
                .clip(CircleShape)
                .clickable { this@GoogleButtonUiContainer.onClick() }
                .border(
                    width = 1.dp,
                    color = Color(0xFF747775),
                    shape = CircleShape
                )
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.android_light_sq_na),
                contentDescription = "구글 계정으로 로그인하기"
            )
            WMText(
                text = "구글 계정으로 로그인하기",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
