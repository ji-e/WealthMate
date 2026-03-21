package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.feature.calendar.component.CalendarFilterModalBottomSheet
import com.jie.wealthmate.feature.calendar.listCalendar.ListCalendar
import com.jie.wealthmate.feature.calendar.monthCalendar.MonthCalendar
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.koin.compose.viewmodel.koinViewModel

val START_DATE = LocalDate(2025, 1, 1)

@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = koinViewModel(),
) {
    // BaseScreen을 사용하여 공통 UI 상태 및 SideEffect 처리
    BaseScreen(
        viewModel = viewModel,
    ) { uiState ->
        CalendarContent(
            uiState = uiState,
            onMonthChanged = viewModel::updateSelectedMonth,
            onDateChanged = viewModel::updateSelectedDate,
            onFilterOptionsChanged = viewModel::updateFilterOptions,
            onTodayClick = { viewModel.updateSelectedMonth() },
            onSearchClick = {
                navController.navigate("search")
            },
            onHistoryClick = { history ->
                navController.navigate("historyDetail/${history.largeCategory}/${history.id}")
            }
        )
    }
}

@Composable
fun CalendarContent(
    uiState: CalendarUiState,
    onMonthChanged: (LocalDate) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onFilterOptionsChanged: (Set<CalendarFilterOption>) -> Unit,
    onTodayClick: () -> Unit,
    onSearchClick: () -> Unit,
    onHistoryClick: (HistoryVo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }
    var isShowFilterBottomSheet by remember { mutableStateOf(false) }

    // 캘린더 월 선택 아이템 리스트 생성
    val monthItems = remember {
        mutableListOf<LocalDate>().apply {
            repeat((today.year - START_DATE.year) * 12 + 12) {
                add(START_DATE.plus(it, DateTimeUnit.MONTH))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 44.dp, bottom = calculateAdjustedToastPadding(124))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MonthCalendar(
                selectedMonth = uiState.selectedMonth,
                selectedDate = uiState.selectedDate,
                historyItems = uiState.histories,
                onMonthChanged = onMonthChanged,
                onTodayClick = onTodayClick,
                onSearchClick = onSearchClick,
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

    // 월 선택 바텀시트
    if (isShowSelectedCalendarModalBottomSheet) {
        WMListSelectionModalBottomSheet(
            title = "월 선택",
            items = monthItems,
            selectedItem = uiState.selectedMonth,
            itemLabel = { it.convertLocalDateToString(formatDateKorYM) },
            onItemSelected = onMonthChanged,
            onDismissRequest = { isShowSelectedCalendarModalBottomSheet = false }
        )
    }

    // 필터 관리 바텀시트
    if (isShowFilterBottomSheet) {
        CalendarFilterModalBottomSheet(
            title = "캘린더 관리",
            selectedOptions = uiState.filterOptions,
            onOptionsSelected = onFilterOptionsChanged,
            onDismissRequest = { isShowFilterBottomSheet = false }
        )
    }
}

@Preview
@Composable
private fun CalendarContentPreview() {
    WMTheme {
        CalendarContent(
            uiState = CalendarUiState(
                selectedMonth = today,
                selectedDate = today,
                histories = emptyList(),
                filterOptions = CalendarFilterOption.entries.toSet()
            ),
            onMonthChanged = {},
            onDateChanged = {},
            onFilterOptionsChanged = {},
            onTodayClick = {},
            onSearchClick = {},
            onHistoryClick = {}
        )
    }
}
