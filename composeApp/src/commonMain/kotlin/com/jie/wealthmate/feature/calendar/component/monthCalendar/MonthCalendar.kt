package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import wealthmate.composeapp.generated.resources.ic_calendar_today
import wealthmate.composeapp.generated.resources.ic_more_vert

private val startDate = LocalDate(2025, 1, 1)

/**
 * MonthCalendar Composable to display a month view calendar.
 * @param selectedMonth 선택된 월 (예: "2024-06")
 * @param selectedDate 선택된 날짜 (예: "2024-06-10")
 * @param onMonthChanged 월이 변경되었을 때 호출되는 콜백
 * @param onClickToday 오늘 날짜 클릭
 * @param onClickSelectedMonth 월 변경 클릭
 * @param onClickDate 선택하고 싶은 날짜 클릭
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonthCalendar(
    modifier: Modifier = Modifier,
    selectedMonth: LocalDate,
    selectedDate: LocalDate,
    onMonthChanged: (LocalDate) -> Unit = {},
    onClickToday: () -> Unit = {},
    onClickSelectedMonth: () -> Unit = {},
    onClickDate: (LocalDate) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    val anchoredState = remember {
        AnchoredDraggableState(
            initialValue = CalendarState.Normal,
            anchors = DraggableAnchors {
                CalendarState.Maximized at 0f
                CalendarState.Normal at -1f
                CalendarState.Minimized at -2f
            },
        )
    }

    // 페이저를 위해 시작 날짜(2025-01-01)와 현재 선택된 월 사이의 개월 수를 계산
    val initialPage = startDate.monthsUntil(selectedMonth)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { (today.year - startDate.year) * 12 + 12 }
    )

    // 사용자가 캘린더를 스와이프했을 때(페이지 변경 감지) -> onMonthChanged 콜백 호출
    LaunchedEffect(pagerState) {
        // 스크롤이 완료된 페이지를 감지하도록 변경
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val newMonth = startDate.plus(
                value = page,
                unit = DateTimeUnit.MONTH
            )
            onMonthChanged(newMonth)
        }
    }

    // 외부 요인으로 selectedMonth가 변경되었을 때(예: '오늘' 버튼 클릭) 페이저를 해당 월로 스크롤
    LaunchedEffect(selectedMonth) {
        val page = startDate.monthsUntil(selectedMonth)
        if (page != pagerState.currentPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(page)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MonthCalendarHeader(
            today = today,
            selectedMonth = selectedMonth,
            onClickToday = onClickToday,
            onClickSelectedMonth = onClickSelectedMonth,
        )
        WeekHeader()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            // 페이지 번호를 기반으로 해당 월을 계산
            val month = startDate.plus(page, DateTimeUnit.MONTH)
            MonthCalendarContent(
                anchoredState = anchoredState,
                today = today,
                selectedDate = selectedDate,
                selectedMonth = month,
                onClickDate = onClickDate
            )
        }
    }
}

@Composable
private fun MonthCalendarHeader(
    today: LocalDate,
    selectedMonth: LocalDate,
    onClickToday: () -> Unit,
    onClickSelectedMonth: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            contentPadding = PaddingValues(horizontal = 12.dp),
            onClick = onClickSelectedMonth
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = selectedMonth.convertLocalDateToString(formatDateKorYM),
                    style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = "년 월 선택",
                    tint = ColorGray.Gray_700
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onClickToday
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar_today),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "오늘"
                )

                WMText(
                    text = today.day.toString(),
                    style = Typography().labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        WMIconButton(
            iconRes = Res.drawable.ic_more_vert,
            contentDescription = "더보기",
            onClick = {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MonthCalendarContent(
    modifier: Modifier = Modifier,
    anchoredState: AnchoredDraggableState<CalendarState>,
    today: LocalDate,
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
    onClickDate: (LocalDate) -> Unit,
) {
    Column(modifier = modifier) {
        DayGrid(
            today = today,
            selectedDate = selectedDate,
            selectedMonth = selectedMonth,
            anchoredState = anchoredState,
            onClickDate = onClickDate
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MonthCalendarPreview() {
    MonthCalendar(
        modifier = Modifier.fillMaxHeight(),
        selectedMonth = today,
        selectedDate = today,
        onMonthChanged = {}
    )
}
