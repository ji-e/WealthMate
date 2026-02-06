@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.googleCloudSync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.HeadLineText
import com.jie.wealthmate.component.InfoText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categoryManagement.component.GoogleLoginButton
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.googleCloudShare.component.BackupAndRestore
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val infoContents = listOf(
                "백업은 사용자의 구글 드라이브 개인 공간에 저장되며,\n사용자 이외는 데이터에 접근할 수 없습니다.",
                "복구 시 현재 기기에 저장된 최신 데이터가 백업 데이터로 대체됩니다. 실행 전 주의해 주세요.",
                "Wi-Fi 환경에서 이용하시는 것을 권장합니다."
            )

            HeadLineText(
                text = "기기를 변경이나 앱을 재설치했을 때\n데이터를 보관하고 불러올 수 있습니다.",
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            infoContents.forEach {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    InfoText(text = "•")
                    InfoText(text = it)
                }
            }

            if (uiState.isLoggedIn) {
                WMTextField(
                    label = "연결된 계정",
                    value = uiState.userName,
                    onValueChange = {},
                    readOnlyColor = ColorGray.Gray_400,
                    readOnly = true,
                    modifier = Modifier.padding(top = 32.dp)
                )

                WMTextField(
                    label = "마지막 백업 날짜",
                    value = uiState.lastSyncDate,
                    onValueChange = {},
                    readOnlyColor = ColorGray.Gray_400,
                    readOnly = true,
                    modifier = Modifier.padding(top = 4.dp)
                )

                BackupAndRestore(
                    modifier = Modifier.padding(top = 12.dp),
                    onBackupClick = screenModel::upload,
                    onRestoreClick = screenModel::download
                )

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    WMText(
                        text = "※ 주의",
                        style = Typography().titleSmall.copy(
                            color = ColorRed.Red_300,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    WMText(text = "복구 시 현재 기기의 데이터는 사라지고 백업 시점의 데이터로 덮어씌워집니다.")
                }
            } else {
                GoogleLoginButton(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 32.dp)
                ) { googleUser ->
                    screenModel.getLastSyncTime()
                    screenModel.getToken(
                        authCode = googleUser.serverAuthCode,
                        email = googleUser.email.default()
                    )
                }
            }
        }
    }
}