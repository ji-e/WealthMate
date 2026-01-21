package com.jie.wealthmate.feature.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categorySetting.categorySetting.CategorySettingScreen
import com.jie.wealthmate.feature.menu.component.MenuContentItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuTitleItem
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

class MenuScreen() : Screen {
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
                        MenuEnum.INCOME_CATEGORY -> navigator.push(
                            CategorySettingScreen(
                                effect.menu
                            )
                        )

                        else -> navigator.push(CategorySettingScreen(effect.menu)) // todo temp
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(uiState.menuEnums.size) { index ->
                val menu = uiState.menuEnums[index]
                MenuTitleItem(menu.label)

                Column() {
                    repeat(menu.items.size) { index ->
                        val menuContent = menu.items[index]
                        MenuContentItem(
                            menu = menuContent,
                            onClickMenu = {
                                screenModel.onMenuClick(menuContent)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun MenuScreenPreview() {
        WMTheme {
            MenuScreen()
        }
    }
}