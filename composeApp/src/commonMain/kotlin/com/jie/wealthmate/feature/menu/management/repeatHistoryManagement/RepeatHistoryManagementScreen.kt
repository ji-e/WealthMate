package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class RepeatHistoryManagementScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: RepeatHistoryManagementScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        Column {
            WMTopBar(
                title = TopBarItem.Title(MenuEnum.REPEAT_HISTORY.title),
                readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_add,
//                        tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
//                        action = {
//                            if (isAddItemEnabled.not()) return@TrailingItem
//                            navigator.push(AddCategoryScreen(largeCategory = largeCategoryItems[pagerState.currentPage]))
//                        }
                    )
                )
            )
        }

    }
}