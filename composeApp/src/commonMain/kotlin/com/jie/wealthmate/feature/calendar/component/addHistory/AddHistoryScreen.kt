package com.jie.wealthmate.feature.calendar.component.addHistory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

class AddHistoryScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddHistoryScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        if (navigator.lastItem is AddHistoryScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("내역 추가")
                )
            }
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