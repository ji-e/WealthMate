package com.jie.wealthmate.feature.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categoryManagement.CategoryManagementScreen
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItem
import com.jie.wealthmate.feature.menu.component.MenuItemData
import com.jie.wealthmate.feature.menu.component.MenuTitleItem
import com.jie.wealthmate.feature.menu.googleCloudShare.GoogleCloudShareScreen
import com.jie.wealthmate.feature.menu.googleCloudSync.GoogleCloudSyncScreen
import com.jie.wealthmate.feature.menu.paymentMethodManagement.PaymentMethodManagementScreen
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject

class MenuScreen(val calculateBottomPadding: Dp) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: MenuScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        if (navigator.lastItem is MenuScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("전체 메뉴")
                )
            }
        }

        screenModel.collectSideEffect { effect ->
            when (effect) {
                is MenuUiSideEffect.OnCLickMenu -> {
                    when (effect.menu) {
                        MenuEnum.CATEGORY -> {
                            navigator.push(CategoryManagementScreen())
                        }

                        MenuEnum.PAYMENT_METHOD -> {
                            navigator.push(PaymentMethodManagementScreen())
                        }

                        MenuEnum.GOOGLE_SYNC -> {
                            navigator.push(GoogleCloudSyncScreen())
                        }

                        MenuEnum.GOOGLE_SHARE -> {
                            navigator.push(GoogleCloudShareScreen())
                        }

                        else -> Unit
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateBottomPadding),
        ) {
            items(uiState.menuEnums.size) { index ->
                val menu = uiState.menuEnums[index]
                
                MenuTitleItem(menu.label)

                if (menu == MenuItemData.Sync) {
                    var label = uiState.userName.ifEmpty { "계정 연결" }

                    GoogleButtonUiContainer(
                        onGoogleSignInResult = { googleUser ->
                            println("googleUser::: $googleUser")
                            label = googleUser?.email.default()
                            screenModel.getToken(
                                authCode = googleUser?.serverAuthCode,
                                email = googleUser?.email.default()
                            )
                        },
                        scopes = listOf(
                            "https://www.googleapis.com/auth/drive.appdata", // 전체 Drive 접근
                            "https://www.googleapis.com/auth/drive.file", // 앱이 생성한 파일
                            "https://www.googleapis.com/auth/drive.metadata.readonly" // 메타데이터 읽기
                        ),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clickable(uiState.userName.isEmpty()) {
                                    this.onClick()
                                }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            WMText(
                                text = label,
                                style = Typography().titleMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }

                Column() {
                    repeat(menu.items.size) { index ->
                        val menuContent = menu.items[index]
                        MenuItem(
                            menu = menuContent,
                            isEnabled = (menu == MenuItemData.Sync && uiState.userName.isEmpty()).not(),
                            onClickMenu = {
                                screenModel.onMenuClick(menuContent)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun MenuScreenPreview() {
        WMTheme {
            MenuScreen(0.dp)
        }
    }
}