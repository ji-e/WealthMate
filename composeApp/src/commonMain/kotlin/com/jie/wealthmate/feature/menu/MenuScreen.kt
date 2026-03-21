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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMMenuButton
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItem
import com.jie.wealthmate.feature.menu.component.MenuItemData
import com.jie.wealthmate.feature.menu.component.MenuTitleItem
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MenuScreen(
    navController: NavController,
    viewModel: MenuViewModel = koinViewModel(),
) {
    BaseScreen(
        viewModel = viewModel,
        onSideEffect = { effect ->
            when (effect) {
                is MenuUiSideEffect.OnCLickMenu -> {
                    when (effect.menu) {
                        MenuEnum.CATEGORY -> navController.navigate("categoryManagement")
                        MenuEnum.PAYMENT_METHOD -> navController.navigate("paymentMethodManagement")
                        MenuEnum.REPEAT_HISTORY -> navController.navigate("repeatHistoryManagement/${LargeCategoryEnum.EXPENSES.name}")
                        MenuEnum.GOOGLE_SYNC -> navController.navigate("googleCloudSync")
                        MenuEnum.GOOGLE_SHARE -> navController.navigate("googleCloudShare")
                        else -> Unit
                    }
                }
            }
        }
    ) { uiState ->
        MenuContent(
            uiState = uiState,
            onMenuClick = viewModel::onMenuClick,
            onUpdateUser = viewModel::updateUser,
            onLogout = viewModel::logout
        )
    }
}

@Composable
fun MenuContent(
    uiState: MenuUiState,
    onMenuClick: (MenuEnum) -> Unit,
    onUpdateUser: (String?, String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        WMTopBar(
            title = TopBarItem.Title("전체 메뉴"),
            readingItem = null
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateAdjustedToastPadding(124)),
            contentPadding = PaddingValues(top = 8.dp, bottom = 20.dp)
        ) {
            items(uiState.menuEnums.size) { index ->
                val menu = uiState.menuEnums[index]

                MenuTitleItem(menu.label)

                if (menu == MenuItemData.Sync) {
                    val isLoggedIn = uiState.userName.isNotEmpty()
                    val label = if (isLoggedIn) uiState.userName else "계정 연결"

                    GoogleButtonUiContainer(
                        onGoogleSignInResult = { googleUser ->
                            onUpdateUser(
                                googleUser?.accessToken.default(),
                                googleUser?.email.default()
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
                                if (isLoggedIn) onLogout()
                                else this.onClick()
                            }
                        )
                    }
                }

                Column {
                    repeat(menu.items.size) { itemIndex ->
                        val menuContent = menu.items[itemIndex]
                        MenuItem(
                            menu = menuContent,
                            onClickMenu = { onMenuClick(menuContent) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
