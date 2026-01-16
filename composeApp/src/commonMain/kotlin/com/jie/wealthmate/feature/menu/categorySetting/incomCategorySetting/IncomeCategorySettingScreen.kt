@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.AddCategoryScreen
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItem
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class IncomeCategorySettingScreen(
    val menuEnum: MenuEnum,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: IncomeCategorySettingScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        BackHandler(true) {
            navigator.pop()
        }

        LaunchedEffect(navigator.lastItem) {
            if (navigator.lastItem is IncomeCategorySettingScreen) {
                screenModel.mainScreenModel.updateTopBar(
                    title = TopBarItem.Title(menuEnum.title),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { navigator.pop() }
                    ),
                    trailingItem = listOf(
                        TopBarItem.TrailingItem(
                            iconRes = Res.drawable.ic_add,
                            action = { navigator.push(AddCategoryScreen(LargeCategoryEnum.INCOME)) }
                        )
                    )
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.incomeCategoryItems.size) { index ->
                val category = uiState.incomeCategoryItems[index]

                CategoryItem(category)
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun IncomeCategorySettingScreenPreview() {
        WMTheme {
            IncomeCategorySettingScreen(MenuEnum.INCOME_CATEGORY)
        }
    }
}