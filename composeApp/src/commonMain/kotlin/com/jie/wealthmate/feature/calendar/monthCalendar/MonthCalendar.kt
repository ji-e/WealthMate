package com.jie.wealthmate.feature.calendar.monthCalendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.CalendarFilterOption
import com.jie.wealthmate.feature.calendar.START_DATE
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.number
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import wealthmate.composeapp.generated.resources.ic_calendar_today
import wealthmate.composeapp.generated.resources.ic_horizontal_rule
import wealthmate.composeapp.generated.resources.ic_more_vert
import wealthmate.composeapp.generated.resources.ic_search

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonthCalendar(
    modifier: Modifier = Modifier,
    selectedMonth: LocalDate,
    selectedDate: LocalDate,
    historyItems: List<HistoryVo> = emptyList(),
    onMonthChanged: (LocalDate) -> Unit = {},
    onTodayClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onSelectedMonthClick: () -> Unit = {},
    onDateClick: (LocalDate) -> Unit = {},
    filterOptions: Set<CalendarFilterOption> = CalendarFilterOption.entries.toSet(),
    bottomContent: @Composable () -> Unit = {},
) {
    // 1. 선택된 월 텍스트 변환 최적화
    val displaySelectedMonthText by remember(selectedMonth) {
        derivedStateOf { selectedMonth.convertLocalDateToString(formatDateKorYM) }
    }

    // 2. 페이저 상태 관리 최적화
    val pageCount = remember { (today.year - START_DATE.year) * 12 + 12 }
    val initialPage = remember { START_DATE.monthsUntil(selectedMonth) }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pageCount }
    )

    // 페이지 변경 시 월 업데이트
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val newMonth = START_DATE.plus(value = page, unit = DateTimeUnit.MONTH)
            if (selectedMonth.month != newMonth.month || selectedMonth.year != newMonth.year) {
                onMonthChanged(newMonth)
            }
        }
    }

    // 외부에서 월 변경 시 페이저 동기화
    LaunchedEffect(selectedMonth) {
        val targetPage = START_DATE.monthsUntil(selectedMonth)
        if (targetPage != pagerState.currentPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    // 3. 월별 데이터 그룹화 최적화
    val historyByMonth = remember(historyItems) {
        historyItems.groupBy { "${it.date.year}-${it.date.month.number}" }
    }

    Column(modifier = modifier.fillMaxSize()) {
        MonthCalendarHeader(
            displaySelectedMonth = displaySelectedMonthText,
            onTodayClick = onTodayClick,
            onSearchClick = onSearchClick,
            onMoreClick = onMoreClick,
            onSelectedMonthClick = onSelectedMonthClick,
        )

        WeekHeader()

        CollapsibleCalendarContent(
            pagerState = pagerState,
            selectedDate = selectedDate,
            historyByMonth = historyByMonth,
            onDateClick = onDateClick,
            filterOptions = filterOptions,
            bottomContent = bottomContent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CollapsibleCalendarContent(
    pagerState: PagerState,
    selectedDate: LocalDate,
    historyByMonth: Map<String, List<HistoryVo>>,
    onDateClick: (LocalDate) -> Unit,
    filterOptions: Set<CalendarFilterOption>,
    bottomContent: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().clipToBounds()
    ) {
        val density = LocalDensity.current
        val dragBarHeight = 32.dp
        val dayHeight = 72.dp

        val maximizedHeightPx = constraints.maxHeight.toFloat()
        val minimizedHeightPx = with(density) { (dayHeight + dragBarHeight).toPx() }
        val normalCalendarHeight = 400.dp
        val normalCalendarHeightPx = with(density) { normalCalendarHeight.toPx() }

        // 드래그 상태 관리
        val anchoredState = remember(maximizedHeightPx, normalCalendarHeightPx, minimizedHeightPx) {
            AnchoredDraggableState(
                initialValue = CalendarStateEnum.Normal,
                anchors = DraggableAnchors {
                    CalendarStateEnum.Maximized at 0f
                    CalendarStateEnum.Normal at -(maximizedHeightPx - normalCalendarHeightPx)
                    CalendarStateEnum.Minimized at -(maximizedHeightPx - minimizedHeightPx)
                },
            )
        }

        val currentOffset = anchoredState.requireOffset()

        // 진행도 계산 최적화
        val expansionProgress by remember(
            currentOffset,
            maximizedHeightPx,
            normalCalendarHeightPx
        ) {
            derivedStateOf {
                val range = maximizedHeightPx - normalCalendarHeightPx
                if (range <= 0) 0f
                else ((currentOffset + range) / range).coerceIn(0f, 1f)
            }
        }

        val collapseProgress by remember(currentOffset, normalCalendarHeightPx, minimizedHeightPx) {
            derivedStateOf {
                val range = normalCalendarHeightPx - minimizedHeightPx
                if (range <= 0) 0f
                else ((currentOffset + maximizedHeightPx - normalCalendarHeightPx) / (minimizedHeightPx - normalCalendarHeightPx))
                    .coerceIn(0f, 1f)
            }
        }

        val dayNormalHeight = (normalCalendarHeight - dragBarHeight)
        val dayMaxHeight = (maxHeight - dragBarHeight)

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.height(with(density) { (maximizedHeightPx + currentOffset).toDp() })
            ) {
                HorizontalPager(
                    modifier = Modifier.weight(1f),
                    state = pagerState,
                    beyondViewportPageCount = 1,
                    key = { it }
                ) { page ->
                    val month = remember(page) { START_DATE.plus(page, DateTimeUnit.MONTH) }
                    val monthHistories = remember(historyByMonth, month) {
                        historyByMonth["${month.year}-${month.month.number}"].default()
                    }

                    DayGrid(
                        selectedDate = selectedDate,
                        selectedMonth = month,
                        historyItems = monthHistories,
                        dayNormalHeight = dayNormalHeight,
                        dayMaxHeight = dayMaxHeight,
                        expansionProgress = expansionProgress,
                        collapseProgress = collapseProgress,
                        filterOptions = filterOptions,
                        onClickDate = onDateClick
                    )
                }

                // 드래그 핸들 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dragBarHeight)
                        .dropShadow(
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                            shadow = Shadow(
                                radius = 8.dp,
                                color = ColorGray.Gray_100,
                                offset = DpOffset(0.dp, (-5).dp)
                            )
                        )
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(ColorGray.White)
                        .anchoredDraggable(anchoredState, Orientation.Vertical),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_horizontal_rule),
                        contentDescription = null,
                        tint = ColorSetting.Info,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }

            // 하단 추가 콘텐츠 (예: 리스트 뷰)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                bottomContent()
            }
        }
    }
}

@Composable
private fun MonthCalendarHeader(
    displaySelectedMonth: String,
    onTodayClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit,
    onSelectedMonthClick: () -> Unit,
) {
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            contentPadding = PaddingValues(horizontal = 12.dp),
            onClick = onSelectedMonthClick
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WMText(
                    text = displaySelectedMonth,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = "년 월 선택",
                    modifier = Modifier.size(24.dp),
                    tint = ColorSetting.Default
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onTodayClick) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar_today),
                    modifier = Modifier.size(24.dp),
                    contentDescription = "오늘"
                )
                WMText(
                    text = today.day.toString(),
                    style = typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        WMIconButton(
            iconRes = Res.drawable.ic_search,
            contentDescription = "검색",
            onClick = onSearchClick
        )
        WMIconButton(
            iconRes = Res.drawable.ic_more_vert,
            contentDescription = "더보기",
            onClick = onMoreClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthCalendarPreview() {
    WMTheme {
        MonthCalendar(
            selectedMonth = today,
            selectedDate = today,
            onMonthChanged = {},
            onDateClick = {}
        )
    }
}
