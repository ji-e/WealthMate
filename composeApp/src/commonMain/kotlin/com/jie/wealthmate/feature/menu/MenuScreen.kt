package com.jie.wealthmate.feature.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.WMMenuButton
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItem
import com.jie.wealthmate.feature.menu.component.MenuItemData
import com.jie.wealthmate.feature.menu.component.MenuTitleItem
import com.jie.wealthmate.feature.menu.data.googleCloudShare.GoogleCloudShareScreen
import com.jie.wealthmate.feature.menu.data.googleCloudSync.GoogleCloudSyncScreen
import com.jie.wealthmate.feature.menu.management.categoryManagement.CategoryManagementScreen
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.PaymentMethodManagementScreen
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import org.koin.compose.koinInject

class MenuScreen(val calculateBottomPadding: Dp) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: MenuScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

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
        Column {
            WMTopBar(
                title = TopBarItem.Title("전체 메뉴"),
                readingItem = null
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = calculateBottomPadding),
                contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp)
            ) {
                items(uiState.menuEnums.size) { index ->
                    val menu = uiState.menuEnums[index]

                    MenuTitleItem(menu.label)

                    if (menu == MenuItemData.Sync) {
                        val isLoggedIn = uiState.userName.isNotEmpty()
                        var label = if (isLoggedIn) "계정 연결 해제" else "계정 연결"

                        GoogleButtonUiContainer(
                            onGoogleSignInResult = { googleUser ->
                                println("googleUser::: $googleUser")
                                label = googleUser?.email.default()

                                screenModel.updateUser(
                                    accessToken = googleUser?.accessToken.default(),
                                    email = googleUser?.email.default()
                                )
                            },
                            scopes = listOf(
                                "https://www.googleapis.com/auth/drive.appdata",
                                "https://www.googleapis.com/auth/drive.file",
                                "https://www.googleapis.com/auth/drive.metadata.readonly"
                            ),
                        ) {
                            WMMenuButton(
                                label = label,
                                onClick = {
                                    if (isLoggedIn) screenModel.logout()
                                    else this.onClick()
                                }
                            )
                        }
                    }

                    Column() {
                        repeat(menu.items.size) { index ->
                            val menuContent = menu.items[index]
                            MenuItem(
                                menu = menuContent,
                                onClickMenu = {
                                    screenModel.onMenuClick(menuContent)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
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