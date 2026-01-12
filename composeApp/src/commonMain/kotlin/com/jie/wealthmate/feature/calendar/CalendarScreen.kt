package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.feature.calendar.component.monthCalendar.MonthCalendar
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


class CalendarScreen(private val calculateBottomPadding: Dp = 0.dp) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CalendarScreenModel() }
        val uiState = screenModel.container.uiState.collectAsState().value


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateBottomPadding),
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