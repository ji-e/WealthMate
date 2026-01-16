package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.calendar.component.monthCalendar.MonthCalendar
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject


class CalendarScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CalendarScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        LaunchedEffect(navigator.lastItem) {
            if (navigator.lastItem is CalendarScreen) {
                screenModel.mainScreenModel.updateTopBar(
                    title = TopBarItem.Title("캘린더")
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MonthCalendar(
                selectedMonth = uiState.selectedMonth,
                selectedDate = uiState.selectedDate,
                onMonthChanged = screenModel::updateSelectedMonth,
                onClickToday = screenModel::updateSelectedMonth,
                onClickDate = screenModel::updateSelectedDate
            )
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun CalendarScreenPreview() {
        WMTheme {
            CalendarScreen()
        }
    }
}