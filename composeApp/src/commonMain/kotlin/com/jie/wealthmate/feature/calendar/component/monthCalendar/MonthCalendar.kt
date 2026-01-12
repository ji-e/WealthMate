package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.today
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import wealthmate.composeapp.generated.resources.ic_calendar_today
import wealthmate.composeapp.generated.resources.ic_more_vert
import kotlin.math.ceil

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

    val pagerState = rememberPagerState(
        initialPage = startDate.monthsUntil(selectedMonth),
        pageCount = { (today.year - startDate.year) * 12 + 12 }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.targetPage }.collect { page ->
            val newMonth = startDate.plus(value = page, unit = DateTimeUnit.MONTH)
            onMonthChanged(newMonth)
        }
    }

    LaunchedEffect(selectedMonth) {
        val page = startDate.monthsUntil(selectedMonth)
        if (page != pagerState.currentPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(page)
            }
        }
    }

    var isInitialSetup by remember { mutableStateOf(true) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        MonthCalendarHeader(
            today = today,
            selectedMonth = selectedMonth,
            onClickToday = onClickToday,
            onClickSelectedMonth = onClickSelectedMonth,
        )
        WeekHeader()

        BoxWithConstraints(
            modifier = Modifier.weight(1f).clipToBounds()
        ) {
            val density = LocalDensity.current
            val dragBarHeight = 32.dp
            val dragBarHeightPx = with(density) { dragBarHeight.toPx() }

            val dayHeight = 60.dp
            val dayHeightPx = with(density) { dayHeight.toPx() }

            val maximizedHeightPx = with(density) { maxHeight.toPx() }
            val minimizedHeightPx = dayHeightPx + dragBarHeightPx
            val normalCalendarHeight = 360.dp

            LaunchedEffect(selectedMonth, maximizedHeightPx) {
                val normalCalendarHeightPx = with(density) { normalCalendarHeight.toPx() }

                anchoredState.updateAnchors(
                    newAnchors = DraggableAnchors {
                        CalendarState.Maximized at 0f
                        CalendarState.Normal at -(maximizedHeightPx - normalCalendarHeightPx)
                        CalendarState.Minimized at -(maximizedHeightPx - minimizedHeightPx)
                    }
                )

                if (isInitialSetup) {
                    anchoredState.snapTo(CalendarState.Normal)
                    isInitialSetup = false
                }
            }

            val currentOffset = anchoredState.requireOffset()
            val normalHeightPx = anchoredState.anchors.positionOf(CalendarState.Normal)
                .takeIf { it.isFinite() }
                ?.let { maximizedHeightPx + it }
                ?: with(density) { normalCalendarHeight.toPx() }

            val expansionProgress =
                if (normalHeightPx == maximizedHeightPx) {
                    0f
                } else {
                    ((currentOffset + maximizedHeightPx - normalHeightPx) / (maximizedHeightPx - normalHeightPx))
                        .coerceIn(0f, 1f)
                }

            val collapseProgress =
                if (normalHeightPx == minimizedHeightPx) {
                    0f
                } else {
                    ((currentOffset + maximizedHeightPx - normalHeightPx) / (minimizedHeightPx - normalHeightPx))
                        .coerceIn(0f, 1f)
                }


            val itemsForCurrentMonth = remember(selectedMonth) {
                mutableListOf<LocalDate?>().apply {
                    val firstDay = selectedMonth.firstDayOfMonth()
                    val startPadding = (firstDay.dayOfWeek.isoDayNumber - 1) % 7
                    repeat(startPadding) { add(null) }
                    add(firstDay)
                    repeat(selectedMonth.lastDayOfMonth().day - 1) {
                        add(
                            firstDay.plus(
                                it + 1,
                                DateTimeUnit.DAY
                            )
                        )
                    }
                }
            }
            val numRowsForCurrentMonth = ceil(itemsForCurrentMonth.size / 7f)
            val dayNormalHeight = (normalCalendarHeight - dragBarHeight) / numRowsForCurrentMonth
            val dayMaxHeight = (maxHeight - dragBarHeight) / numRowsForCurrentMonth

            Column(
                modifier = Modifier.height(with(density) { (maximizedHeightPx + currentOffset).toDp() })
            ) {

                HorizontalPager(
                    modifier = Modifier.weight(1f),
                    state = pagerState,
                ) { page ->
                    val month = startDate.plus(page, DateTimeUnit.MONTH)
                    MonthCalendarContent(
                        today = today,
                        selectedDate = selectedDate,
                        selectedMonth = month,
                        dayNormalHeight = dayNormalHeight,
                        dayMaxHeight = dayMaxHeight,
                        expansionProgress = expansionProgress,
                        collapseProgress = collapseProgress,
                        onClickDate = onClickDate
                    )
                }

                // dragBar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dragBarHeight)
                        .background(ColorGray.Gray_300)
                        .anchoredDraggable(
                            state = anchoredState,
                            orientation = Orientation.Vertical
                        )
                )
            }
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

@Composable
private fun MonthCalendarContent(
    modifier: Modifier = Modifier,
    today: LocalDate,
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
    dayNormalHeight: Dp,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    onClickDate: (LocalDate) -> Unit,
) {
    Column(modifier = modifier) {
        DayGrid(
            today = today,
            selectedDate = selectedDate,
            selectedMonth = selectedMonth,
            dayNormalHeight = dayNormalHeight,
            dayMaxHeight = dayMaxHeight,
            expansionProgress = expansionProgress,
            collapseProgress = collapseProgress,
            onClickDate = onClickDate
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun MonthCalendarPreview() {
    MonthCalendar(
        selectedMonth = today,
        selectedDate = today,
        onMonthChanged = {}
    )
}
