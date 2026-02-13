package com.jie.wealthmate.feature.calendar.component.monthCalendar

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
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.startDate
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.convertLocalDateToString
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonthCalendar(
    modifier: Modifier = Modifier,
    selectedMonth: LocalDate,
    selectedDate: LocalDate,
    historyItems: List<HistoryVo> = emptyList(),
    onMonthChanged: (LocalDate) -> Unit = {},
    onTodayClick: () -> Unit = {},
    onSelectedMonthClick: () -> Unit = {},
    onDateClick: (LocalDate) -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
) {
    var displaySelectedMonth by remember {
        mutableStateOf(selectedMonth.convertLocalDateToString(formatDateKorYM))
    }

    val pagerState = rememberPagerState(
        initialPage = remember { startDate.monthsUntil(selectedMonth) },
        pageCount = { (today.year - startDate.year) * 12 + 12 }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val newMonth = startDate.plus(value = page, unit = DateTimeUnit.MONTH)
            if (displaySelectedMonth != newMonth.convertLocalDateToString(formatDateKorYM)) {
                displaySelectedMonth = newMonth.convertLocalDateToString(formatDateKorYM)
                onMonthChanged(newMonth)
            }
        }
    }

    LaunchedEffect(selectedMonth) {
        val targetPage = startDate.monthsUntil(selectedMonth)
        if (targetPage != pagerState.currentPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    val historyByMonth = remember(historyItems) {
        historyItems.groupBy { "${it.date.year}-${it.date.month.number}" }
    }

    Column(modifier = modifier.fillMaxSize()) {
        MonthCalendarHeader(
            displaySelectedMonth = displaySelectedMonth,
            onTodayClick = onTodayClick,
            onSelectedMonthClick = onSelectedMonthClick,
        )
        WeekHeader()

        CollapsibleCalendarContent(
            pagerState = pagerState,
            selectedDate = selectedDate,
            historyByMonth = historyByMonth,
            onDateClick = onDateClick,
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

        val anchoredState = remember(maximizedHeightPx) {
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

        val normalHeightPx = remember(anchoredState.anchors, maximizedHeightPx, normalCalendarHeightPx) {
            anchoredState.anchors.positionOf(CalendarStateEnum.Normal).let {
                if (it.isFinite()) maximizedHeightPx + it else normalCalendarHeightPx
            }
        }

        val expansionProgress = remember(currentOffset, maximizedHeightPx, normalHeightPx) {
            if (normalHeightPx == maximizedHeightPx) 0f
            else ((currentOffset + maximizedHeightPx - normalHeightPx) / (maximizedHeightPx - normalHeightPx)).coerceIn(0f, 1f)
        }

        val collapseProgress = remember(currentOffset, normalHeightPx, minimizedHeightPx) {
            if (normalHeightPx == minimizedHeightPx) 0f
            else ((currentOffset + maximizedHeightPx - normalHeightPx) / (minimizedHeightPx - normalHeightPx)).coerceIn(0f, 1f)
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
                    beyondViewportPageCount = 0,
                    key = { it }
                ) { page ->
                    val month = remember(page) { startDate.plus(page, DateTimeUnit.MONTH) }
                    val monthHistories = remember(historyByMonth, month) {
                        historyByMonth["${month.year}-${month.month.number}"] ?: emptyList()
                    }

                    MonthCalendarContent(
                        selectedDate = selectedDate,
                        selectedMonth = month,
                        historyItems = monthHistories,
                        dayNormalHeight = dayNormalHeight,
                        dayMaxHeight = dayMaxHeight,
                        expansionProgress = expansionProgress,
                        collapseProgress = collapseProgress,
                        onClickDate = onDateClick
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dragBarHeight)
                        .dropShadow(
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                            shadow = Shadow(radius = 8.dp, color = ColorGray.Gray_100, offset = DpOffset(0.dp, (-5).dp))
                        )
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(ColorGray.White)
                        .anchoredDraggable(anchoredState, Orientation.Vertical),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_horizontal_rule),
                        contentDescription = null,
                        tint = ColorGray.Gray_400,
                        modifier = Modifier.size(40.dp),
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                bottomContent()
            }
        }
    }
}

@Composable
private fun MonthCalendarHeader(
    displaySelectedMonth: String,
    onTodayClick: () -> Unit,
    onSelectedMonthClick: () -> Unit,
) {
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
                    style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold, color = ColorGray.Gray_700)
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = "년 월 선택",
                    modifier = Modifier.size(24.dp),
                    tint = ColorGray.Gray_700
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onTodayClick) {
            Box(contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(Res.drawable.ic_calendar_today), modifier = Modifier.size(24.dp), contentDescription = "오늘")
                WMText(
                    text = today.day.toString(),
                    style = Typography().labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        WMIconButton(iconRes = Res.drawable.ic_more_vert, contentDescription = "더보기", onClick = {})
    }
}

@Composable
private fun MonthCalendarContent(
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
    historyItems: List<HistoryVo>,
    dayNormalHeight: Dp,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    onClickDate: (LocalDate) -> Unit,
) {
    DayGrid(
        selectedDate = selectedDate,
        selectedMonth = selectedMonth,
        historyItems = historyItems,
        dayNormalHeight = dayNormalHeight,
        dayMaxHeight = dayMaxHeight,
        expansionProgress = expansionProgress,
        collapseProgress = collapseProgress,
        onClickDate = onClickDate
    )
}
