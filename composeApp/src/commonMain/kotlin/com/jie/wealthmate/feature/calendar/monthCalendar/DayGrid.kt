package com.jie.wealthmate.feature.calendar.monthCalendar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.CalendarFilterOption
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.math.abs
import kotlin.math.ceil

@Composable
fun DayGrid(
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
    historyItems: List<HistoryVo>,
    dayNormalHeight: Dp,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    filterOptions: Set<CalendarFilterOption>,
    onClickDate: (LocalDate) -> Unit,
) {
    val historyByDate = remember(historyItems) {
        historyItems.groupBy { it.date }
    }

    val items = remember(selectedMonth) {
        mutableListOf<Pair<LocalDate, MonthPeriodEnum>>().apply {
            val firstDay = selectedMonth.firstDayOfMonth()
            val startPadding = firstDay.dayOfWeek.isoDayNumber % 7

            repeat(startPadding) {
                add(
                    firstDay.minus(
                        startPadding - it,
                        DateTimeUnit.DAY
                    ) to MonthPeriodEnum.LAST_MONTH
                )
            }
            repeat(selectedMonth.lastDayOfMonth().day) {
                add(firstDay.plus(it, DateTimeUnit.DAY) to MonthPeriodEnum.THIS_MONTH)
            }
            val itemsSize = this.size
            val targetSize = if (itemsSize <= 35) 35 else 42
            repeat(targetSize - itemsSize) {
                add(
                    firstDay.lastDayOfMonth()
                        .plus(it + 1, DateTimeUnit.DAY) to MonthPeriodEnum.NEXT_MONTH
                )
            }
        }
    }

    val numRowsForCurrentMonth = ceil(items.size / 7f)
    val selectedItemIndex = remember(items, selectedDate) {
        items.indexOfFirst { it.first == selectedDate }
    }
    val selectedRowIndex = remember(selectedItemIndex) {
        if (selectedItemIndex < 0) 0 else selectedItemIndex / 7
    }

    val animatedRowIndex by animateFloatAsState(
        targetValue = selectedRowIndex.toFloat(),
        label = "selectedRowIndexAnimation"
    )

    if (items.isNotEmpty()) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(horizontal = 4.dp),
            userScrollEnabled = false
        ) {
            items(
                count = items.size,
                key = { index -> items[index].first.toString() }
            ) { index ->
                val date = items[index].first
                val dayHistory = historyByDate[date] ?: emptyList()

                val repeatItems =
                    remember(dayHistory) { dayHistory.filter { it.repeatCycle != null } }
                
                // 1. 수입 합계 계산 (독립 필터링)
                val incomeAmount = remember(dayHistory, filterOptions) {
                    // '수입 노출' 스위치가 꺼져있으면 0
                    if (!filterOptions.contains(CalendarFilterOption.SHOW_INCOME)) 0L
                    else dayHistory.filter { it.largeCategory == LargeCategoryEnum.INCOME }
                        .filter { it.category?.isFixed != true || filterOptions.contains(CalendarFilterOption.INCLUDE_FIXED_INCOME) }
                        .sumOf { it.amount }
                }
                
                // 2. 지출 합계 계산 (독립 필터링)
                val expenseAmount = remember(dayHistory, filterOptions) {
                    // '지출 노출' 스위치가 꺼져있으면 지출 및 저축 모두 합산 제외
                    if (!filterOptions.contains(CalendarFilterOption.SHOW_EXPENSES)) 0L
                    else dayHistory.filter { 
                        when(it.largeCategory) {
                            LargeCategoryEnum.EXPENSES -> {
                                // 지출 고정 카테고리 포함 여부 확인
                                it.category?.isFixed != true || filterOptions.contains(CalendarFilterOption.INCLUDE_FIXED_EXPENSES)
                            }
                            LargeCategoryEnum.SAVING -> {
                                // 저축 포함 여부 및 저축 고정 카테고리 포함 여부 확인
                                val isShowSavings = filterOptions.contains(CalendarFilterOption.SHOW_SAVINGS)
                                isShowSavings && (it.category?.isFixed != true || filterOptions.contains(CalendarFilterOption.INCLUDE_FIXED_SAVINGS))
                            }
                            else -> false
                        }
                    }.sumOf { it.amount }
                }

                DayItem(
                    day = items[index],
                    isSelected = date == selectedDate,
                    rowIndex = index / 7,
                    animatedRowIndex = animatedRowIndex,
                    incomeAmount = incomeAmount,
                    expenseAmount = expenseAmount,
                    repeatItems = repeatItems,
                    dayNormalHeight = dayNormalHeight / numRowsForCurrentMonth,
                    dayMaxHeight = dayMaxHeight / numRowsForCurrentMonth,
                    expansionProgress = expansionProgress,
                    collapseProgress = collapseProgress,
                    showRepeatHistory = filterOptions.contains(CalendarFilterOption.SHOW_REPEAT),
                    onClickDate = onClickDate
                )
            }
        }
    }
}

@Composable
internal fun DayItem(
    modifier: Modifier = Modifier,
    day: Pair<LocalDate, MonthPeriodEnum>,
    isSelected: Boolean,
    rowIndex: Int,
    animatedRowIndex: Float,
    incomeAmount: Long,
    expenseAmount: Long,
    repeatItems: List<HistoryVo>,
    dayNormalHeight: Dp,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    showRepeatHistory: Boolean,
    onClickDate: (LocalDate) -> Unit,
) {
    val selectionFactor = (1f - abs(rowIndex - animatedRowIndex)).coerceIn(0f, 1f)

    val height = when {
        collapseProgress > 0f -> {
            val minHeight = 72.dp * selectionFactor
            lerp(start = dayNormalHeight, stop = minHeight, fraction = collapseProgress)
        }

        expansionProgress > 0f -> {
            lerp(start = dayNormalHeight, stop = dayMaxHeight, fraction = expansionProgress)
        }

        else -> dayNormalHeight
    }

    val alpha = if (collapseProgress > 0f) {
        1f - (collapseProgress * (1f - selectionFactor))
    } else 1f

    Column(
        modifier = modifier
            .alpha(alpha)
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
            .clickable { onClickDate(day.first) }
            .padding(2.dp),
    ) {
        val isToday = day.first == today
        val dayColor = when {
            isToday -> ColorGray.White
            day.second != MonthPeriodEnum.THIS_MONTH -> ColorGray.Gray_200
            else -> WeekEnum.creator(day.first.dayOfWeek.isoDayNumber).color
        }
        val dayBackgroundColor = when {
            isToday -> WeekEnum.creator(day.first.dayOfWeek.isoDayNumber).color
            isSelected -> ColorPrimary.Primary_200
            else -> ColorGray.White
        }

        WMText(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .background(color = dayBackgroundColor, shape = CircleShape)
                .align(Alignment.CenterHorizontally)
                .width(24.dp),
            text = day.first.day.toString(),
            style = Typography().bodySmall.copy(
                color = dayColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )

        if (incomeAmount > 0) {
            WMText(
                modifier = Modifier.fillMaxWidth().height(14.dp),
                text = "+${formatWithCommas(incomeAmount.toString())}",
                style = Typography().labelSmall.copy(color = ColorBlue.Blue_300),
                textAlign = TextAlign.Center,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 9.sp,
                    maxFontSize = 11.sp,
                    stepSize = 1.sp
                )
            )
        }
        if (expenseAmount > 0) {
            WMText(
                modifier = Modifier.fillMaxWidth().height(14.dp),
                text = "-${formatWithCommas(expenseAmount.toString())}",
                style = Typography().labelSmall.copy(color = ColorRed.Red_300),
                textAlign = TextAlign.Center,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 9.sp,
                    maxFontSize = 11.sp,
                    stepSize = 1.sp
                )
            )
        }

        if (showRepeatHistory && repeatItems.isNotEmpty() && height > 60.dp) {
            val itemHeight = 14.dp
            val availableHeight = height - 40.dp
            val maxVisibleItems =
                (availableHeight / itemHeight).toInt().coerceAtMost(repeatItems.size)

            if (maxVisibleItems > 0) {
                Column {
                    repeatItems.take(maxVisibleItems).forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(itemHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(item.largeCategory.accentColor)
                            )
                            WMText(
                                text = item.content.default(),
                                style = Typography().labelSmall,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 2.dp),
                                autoSize = TextAutoSize.StepBased(
                                    minFontSize = 9.sp,
                                    maxFontSize = 11.sp,
                                    stepSize = 1.sp
                                )
                            )
                        }
                    }
                    if (repeatItems.size > maxVisibleItems) {
                        WMText(
                            modifier = Modifier.fillMaxWidth().height(14.dp),
                            text = "...",
                            style = Typography().labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
