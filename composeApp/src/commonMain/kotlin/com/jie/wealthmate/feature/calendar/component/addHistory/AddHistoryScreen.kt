@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.calendar.component.addHistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.calendar.component.addHistory.component.LargeCategorySelectBox
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

class AddHistoryScreen() : BaseScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddHistoryScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value


        fun onBack() {
            showSaveBackDialog(uiState.isChangedData) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        if (navigator.lastItem is AddHistoryScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("내역 추가"),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { onBack() }
                    ),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            LargeCategorySelectBox()

        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AddHistoryScreenPreview() {
    WMTheme {
        AddHistoryScreen()
    }
}