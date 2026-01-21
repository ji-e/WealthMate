package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.calendar.component.monthCalendar.MonthCalendar
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject


class CalendarScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CalendarScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        if (navigator.lastItem is CalendarScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("캘린더")
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateAdjustedToastPadding(80)),
        ) {
            MonthCalendar(
                selectedMonth = uiState.selectedMonth,
                selectedDate = uiState.selectedDate,
                onMonthChanged = screenModel::updateSelectedMonth,
                onClickToday = screenModel::updateSelectedMonth,
                onClickDate = screenModel::updateSelectedDate
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorGray.Gray_200),
                    contentAlignment = Alignment.Center
                ) {
                    WMText(text = "리스트뷰 영역 (ListView Area)")
                }
            }
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