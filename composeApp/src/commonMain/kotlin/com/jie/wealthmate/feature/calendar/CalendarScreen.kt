package com.jie.wealthmate.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.calendar.addHistory.AddHistoryScreen
import com.jie.wealthmate.feature.calendar.component.monthCalendar.MonthCalendar
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.yearMonth
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

val startDate = LocalDate(2025, 1, 1)

class CalendarScreen() : Screen {
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
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CalendarScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }

        if (navigator.lastItem is CalendarScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("캘린더")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateAdjustedToastPadding(80))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
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
                        WMText(text = "리스트뷰 영역 (ListView Area)")
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
                onClick = { navigator.push(AddHistoryScreen()) }
            )

            if (isShowSelectedCalendarModalBottomSheet) {
                SelectedCalendarModalBottomSheet(
                    selectedMonth = uiState.selectedMonth,
                    onMonthChange = screenModel::updateSelectedMonth,
                    onDismissRequest = { isShowSelectedCalendarModalBottomSheet = false }
                )
            }
        }
    }

    /**
     * 월 선택 ModalBottomSheet
     */
    @Composable
    private fun SelectedCalendarModalBottomSheet(
        selectedMonth: LocalDate = today,
        onMonthChange: (LocalDate) -> Unit = {},
        onDismissRequest: () -> Unit = {},
    ) {
        val listState = rememberLazyListState()
        var tempSelectedMonth by remember { mutableStateOf(selectedMonth) }

        WMModalBottomSheet(
            title = "월 선택",
            onDismissRequest = { onDismissRequest() },
        ) {

            LaunchedEffect(Unit) {
                val movePosition = monthItem.indexOf(tempSelectedMonth.firstDayOfMonth())
                    .run { if (this <= 0) 0 else this - 1 }

                listState.scrollToItem(movePosition)
            }

            LazyColumn(
                modifier = Modifier.height(240.dp)
                    .nestedScroll(object :
                        NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource,
                        ): Offset {
                            // 위로 스크롤하거나 아래로 스크롤할 때 시트가 움직이지 않도록 이벤트를 여기서 소비하지 않음
                            return super.onPreScroll(available, source)
                        }
                    }),
                state = listState
            ) {
                items(monthItem.size) {
                    val month = monthItem[it]
                    val isSelected = month.yearMonth == tempSelectedMonth.yearMonth

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
                            .clickable { tempSelectedMonth = month },
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = month.convertLocalDateToString(formatDateKorYM),
                            style = Typography().bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
                                fontSize = if (isSelected) 18.sp else 16.sp
                            ),

                            )
                    }
                }
            }
            WMButton(
                text = "확인",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                onClick = {
                    onMonthChange(tempSelectedMonth)
                    onDismissRequest()
                }
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