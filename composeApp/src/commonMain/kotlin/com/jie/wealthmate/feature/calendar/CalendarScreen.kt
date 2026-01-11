package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.feature.calendar.component.monthCalendar.MonthCalendar
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.today
import org.jetbrains.compose.ui.tooling.preview.Preview


class CalendarScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { CalendarScreenModel() }
        val counter by screenModel.counter.collectAsState()


        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MonthCalendar(
                selectedMonth = today,
                selectedDate = today,
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