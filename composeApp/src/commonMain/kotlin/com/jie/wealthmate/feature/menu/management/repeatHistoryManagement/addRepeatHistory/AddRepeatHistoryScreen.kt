package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory

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

class AddRepeatHistoryScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddRepeatHistoryScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        Column {
            WMTopBar(
                title = TopBarItem.Title("${MenuEnum.REPEAT_HISTORY.label} 추가"),
                readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
            )
        }
    }
}