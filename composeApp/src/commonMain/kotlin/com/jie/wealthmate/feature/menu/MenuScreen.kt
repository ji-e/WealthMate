package com.jie.wealthmate.feature.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categoryManagement.CategoryManagementScreen
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuItem
import com.jie.wealthmate.feature.menu.component.MenuTitleItem
import com.jie.wealthmate.feature.menu.googleCloudShare.GoogleCloudShareScreen
import com.jie.wealthmate.feature.menu.googleCloudSync.GoogleCloudSyncScreen
import com.jie.wealthmate.feature.menu.paymentMethodManagement.PaymentMethodManagementScreen
import com.jie.wealthmate.theme.WMTheme
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