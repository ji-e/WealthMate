package com.jie.wealthmate.feature.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting.IncomeCategorySettingScreen
import com.jie.wealthmate.feature.menu.component.MenuContentItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.component.MenuTitleItem
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

class MenuScreen(private val calculateBottomPadding: Dp = 0.dp) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MenuScreenModel() }
        val uiState = screenModel.container.uiState.collectAsState().value

        screenModel.collectSideEffect { effect ->
            when (effect) {
                is MenuUiSideEffect.OnCLickMenu -> {
                    when (effect.menu) {
                        MenuEnum.INCOME_CATEGORY -> navigator.push(IncomeCategorySettingScreen(effect.menu))
                        else -> navigator.push(IncomeCategorySettingScreen(effect.menu)) // todo temp
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateBottomPadding)
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