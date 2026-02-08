package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.calendar.addHistory.AddHistoryScreen
import com.jie.wealthmate.feature.calendar.component.SelectedCalendarModalBottomSheet
import com.jie.wealthmate.feature.calendar.component.listCalendar.ListCalendar
import com.jie.wealthmate.feature.calendar.component.monthCalendar.MonthCalendar
import com.jie.wealthmate.feature.calendar.historyDetail.HistoryDetailScreen
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

val startDate = LocalDate(2025, 1, 1)

class CalendarScreen() : BaseScreen() {
    val monthItem = mutableListOf<LocalDate>().apply {
        repeat((today.year - startDate.year) * 12 + 12) {
            add(
                startDate.plus(
                    it,
                    DateTimeUnit.MONTH
                )
            )
        }
    }

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CalendarScreenModel = koinScreenModel()
        val uiState = screenModel.container.uiState.collectAsState().value

        var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateAdjustedToastPadding(80))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                WMTopBar(
                    title = TopBarItem.Title("캘린더")
                )

                MonthCalendar(
                    selectedMonth = uiState.selectedMonth,
                    selectedDate = uiState.selectedDate,
                    historyItems = uiState.histories,
                    onMonthChanged = screenModel::updateSelectedMonth,
                    onTodayClick = screenModel::updateSelectedMonth,
                    onSelectedMonthClick = { isShowSelectedCalendarModalBottomSheet = true },
                    onDateClick = screenModel::updateSelectedDate
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ListCalendar(
                            selectedDate = uiState.selectedDate,
                            historyItems = uiState.histories,
                            onDateSelected = screenModel::updateSelectedDate,
                            onHistoryClick = {
                                navigator.push(
                                    HistoryDetailScreen(
                                        largeCategory = it.largeCategory,
                                        historyId = it.id
                                    )
                                )
                            },
                            emptyContent = {
                                EmptyListView(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 20.dp, horizontal = 28.dp),
                                    contentText = "내역이 없습니다.",
                                )
                            }
                        )
                    }
                }
            }

            WMIconButton(
                iconRes = Res.drawable.ic_add,
                contentDescription = "내역 추가",
                iconButtonModifier = Modifier
                    .padding(20.dp)
                    .dropShadow(
                        shape = CircleShape,
                        shadow = Shadow(
                            radius = 4.dp,
                            spread = 0.dp,
                            color = ColorGray.Gray_200,
                            offset = DpOffset(x = 2.dp, 2.dp)
                        )
                    )
                    .clip(CircleShape)
                    .background(ColorPrimary.Primary_500)
                    .align(Alignment.BottomEnd),
                tint = ColorGray.White,
                onClick = { navigator.push(AddHistoryScreen(uiState.selectedDate)) }
            )
        }

        if (isShowSelectedCalendarModalBottomSheet) {
            SelectedCalendarModalBottomSheet(
                monthItem = monthItem,
                selectedMonth = uiState.selectedMonth,
                onMonthChange = screenModel::updateSelectedMonth,
                onDismissRequest = { isShowSelectedCalendarModalBottomSheet = false }
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