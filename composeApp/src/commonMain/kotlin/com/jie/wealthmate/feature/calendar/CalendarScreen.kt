package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.feature.calendar.component.CalendarFilterModalBottomSheet
import com.jie.wealthmate.feature.calendar.component.SelectedCalendarModalBottomSheet
import com.jie.wealthmate.feature.calendar.component.listCalendar.ListCalendar
import com.jie.wealthmate.feature.calendar.component.monthCalendar.MonthCalendar
import com.jie.wealthmate.feature.calendar.historyDetail.HistoryDetailScreen
import com.jie.wealthmate.feature.search.SearchScreen
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

val startDate = LocalDate(2025, 1, 1)

class CalendarScreen() : BaseScreen() {

    private val listState = LazyListState()

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CalendarScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }
        var isShowFilterBottomSheet by remember { mutableStateOf(false) }

        val monthItems = remember {
            mutableListOf<LocalDate>().apply {
                repeat((today.year - startDate.year) * 12 + 12) {
                    add(startDate.plus(it, DateTimeUnit.MONTH))
                }
            }
        }

        val onMonthChanged =
            remember { { month: LocalDate -> screenModel.updateSelectedMonth(month) } }
        val onDateChanged = remember { { date: LocalDate -> screenModel.updateSelectedDate(date) } }
        val onHistoryClick = remember {
            { history: HistoryVo ->
                navigator.push(
                    HistoryDetailScreen(
                        largeCategory = history.largeCategory,
                        historyId = history.id
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp, bottom = calculateAdjustedToastPadding(80))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                MonthCalendar(
                    selectedMonth = uiState.selectedMonth,
                    selectedDate = uiState.selectedDate,
                    historyItems = uiState.histories,
                    onMonthChanged = onMonthChanged,
                    onTodayClick = { screenModel.updateSelectedMonth() },
                    onSearchClick = { navigator.push(SearchScreen()) },
                    onMoreClick = { isShowFilterBottomSheet = true },
                    onSelectedMonthClick = { isShowSelectedCalendarModalBottomSheet = true },
                    onDateClick = onDateChanged,
                    filterOptions = uiState.filterOptions,
                    bottomContent = {
                        ListCalendar(
                            selectedDate = uiState.selectedDate,
                            historyItems = uiState.histories,
                            onDateSelected = onDateChanged,
                            onHistoryClick = onHistoryClick,
                            listState = listState,
                        )
                    }
                )
            }
        }

        if (isShowSelectedCalendarModalBottomSheet) {
            SelectedCalendarModalBottomSheet(
                monthItem = monthItems,
                selectedMonth = uiState.selectedMonth,
                onMonthChange = { month ->
                    onMonthChanged(month)
                    isShowSelectedCalendarModalBottomSheet = false
                },
                onDismissRequest = { isShowSelectedCalendarModalBottomSheet = false }
            )
        }

        if (isShowFilterBottomSheet) {
            CalendarFilterModalBottomSheet(
                title = "캘린더 관리",
                selectedOptions = uiState.filterOptions,
                onOptionsSelected = { options ->
                    screenModel.updateFilterOptions(options)
                },
                onDismissRequest = { isShowFilterBottomSheet = false }
            )
        }
    }
}
