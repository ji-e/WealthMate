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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.CalendarFilterOption
import com.jie.wealthmate.feature.calendar.component.vo.DayItemDataVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
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
    // 1. 달력 날짜 생성 로직 최적화
    val days = remember(selectedMonth) {
        val firstDay = selectedMonth.firstDayOfMonth()
        val startPadding = firstDay.dayOfWeek.isoDayNumber % 7
        val lastDay = selectedMonth.lastDayOfMonth()

        val list = mutableListOf<Pair<LocalDate, MonthPeriodEnum>>()
        // 이전 달 채우기
        repeat(startPadding) {
            val date = firstDay.minus(startPadding - it, DateTimeUnit.DAY)
            list.add(date to MonthPeriodEnum.LAST_MONTH)
        }
        // 이번 달 채우기
        repeat(lastDay.day) {
            val date = firstDay.plus(it, DateTimeUnit.DAY)
            list.add(date to MonthPeriodEnum.THIS_MONTH)
        }
        // 다음 달 채우기 (그리드 균형을 위해 35 또는 42개로 고정)
        val targetSize = if (list.size <= 35) 35 else 42
        val nextDaysCount = targetSize - list.size
        repeat(nextDaysCount) {
            val date = lastDay.plus(it + 1, DateTimeUnit.DAY)
            list.add(date to MonthPeriodEnum.NEXT_MONTH)
        }
        list
    }

    // 2. 일별 데이터(금액, 반복내역 등)를 미리 계산하여 렌더링 최적화
    val dayItemDataList = remember(days, historyItems, filterOptions) {
        val historyByDate = historyItems.groupBy { it.date }
        days.mapIndexed { index, (date, period) ->
            val dayHistory = historyByDate[date] ?: emptyList()

            // 수입 합계
            val incomeAmount =
                if (CalendarFilterOption.SHOW_INCOME in filterOptions) {
                    dayHistory.sumOf { history ->
                        val isEligible = history.largeCategory == LargeCategoryEnum.INCOME &&
                                (history.category?.isFixed != true || CalendarFilterOption.INCLUDE_FIXED_INCOME in filterOptions)
                        if (isEligible) history.amount else 0L
                    }
                } else {
                    0L
                }

            // 지출 합계 (저축 포함 여부 체크)
            val expenseAmount =
                if (CalendarFilterOption.SHOW_EXPENSES in filterOptions) {
                    dayHistory.sumOf { history ->
                        val isEligible = when (history.largeCategory) {
                            LargeCategoryEnum.EXPENSES -> {
                                history.category?.isFixed != true || CalendarFilterOption.INCLUDE_FIXED_EXPENSES in filterOptions
                            }

                            LargeCategoryEnum.SAVING -> {
                                CalendarFilterOption.SHOW_SAVINGS in filterOptions &&
                                        (history.category?.isFixed != true || CalendarFilterOption.INCLUDE_FIXED_SAVINGS in filterOptions)
                            }

                            else -> false
                        }
                        if (isEligible) history.amount else 0L
                    }
                } else {
                    0L
                }

            // 반복 내역 필터링
            val repeatItems =
                if (CalendarFilterOption.SHOW_REPEAT in filterOptions) {
                    dayHistory.filter { it.repeatCycle != null }
                } else {
                    emptyList()
                }

            DayItemDataVo(
                date = date,
                period = period,
                incomeAmount = incomeAmount,
                expenseAmount = expenseAmount,
                repeatItems = repeatItems,
                rowIndex = index / 7
            )
        }
    }

    val numRows = days.size / 7
    val selectedRowIndex = remember(days, selectedDate) {
        val index = days.indexOfFirst { it.first == selectedDate }
        if (index < 0) 0 else index / 7
    }

    val animatedRowIndex by animateFloatAsState(
        targetValue = selectedRowIndex.toFloat(),
        label = "selectedRowIndexAnimation"
    )

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(horizontal = Padding.SpacerXXS),
        userScrollEnabled = false
    ) {
        items(
            count = dayItemDataList.size,
            key = { index -> dayItemDataList[index].date.toString() }
        ) { index ->
            val data = dayItemDataList[index]
            DayItem(
                data = data,
                isSelected = data.date == selectedDate,
                animatedRowIndex = animatedRowIndex,
                dayNormalHeight = dayNormalHeight / numRows,
                dayMaxHeight = dayMaxHeight / numRows,
                expansionProgress = expansionProgress,
                collapseProgress = collapseProgress,
                onClickDate = onClickDate
            )
        }
    }
}

@Composable
internal fun DayItem(
    modifier: Modifier = Modifier,
    data: DayItemDataVo,
    isSelected: Boolean,
    animatedRowIndex: Float,
    dayNormalHeight: Dp,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    onClickDate: (LocalDate) -> Unit,
) {
    val selectionFactor = (1f - abs(data.rowIndex - animatedRowIndex)).coerceIn(0f, 1f)

    // 애니메이션 상태에 따른 높이 계산
    val height = when {
        collapseProgress > 0f -> {
            val minHeight = 72.dp * selectionFactor
            lerp(start = dayNormalHeight, stop = minHeight, fraction = collapseProgress)
        }

        expansionProgress > 0f -> {
            lerp(start = dayNormalHeight, stop = dayMaxHeight, fraction = expansionProgress)
        }

        else -> {
            dayNormalHeight
        }
    }

    // 주간 뷰 전환 시 비선택 행 투명도 조절
    val alpha =
        if (collapseProgress > 0f) 1f - (collapseProgress * (1f - selectionFactor))
        else 1f

    Column(
        modifier = modifier
            .alpha(alpha)
            .fillMaxWidth()
            .height(height)
            .clip(Shapes.small)
            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
            .clickable { onClickDate(data.date) }
            .padding(2.dp),
    ) {
        val isToday = data.date == today
        val dayColor = when {
            isToday -> ColorGray.White
            data.period != MonthPeriodEnum.THIS_MONTH -> ColorGray.Gray_200
            else -> ColorSetting.Info
        }
        val dayBackgroundColor = when {
            isToday -> ColorSetting.Info
            else -> Color.Transparent
        }

        WMText(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .background(color = dayBackgroundColor, shape = CircleShape)
                .align(Alignment.CenterHorizontally)
                .width(24.dp),
            text = data.date.day.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = dayColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        if (data.incomeAmount > 0) {
            AmountText(
                amount = data.incomeAmount,
                prefix = "+",
                color = LargeCategoryEnum.INCOME.accentColor
            )
        }
        if (data.expenseAmount > 0) {
            AmountText(
                amount = data.expenseAmount,
                prefix = "-",
                color = LargeCategoryEnum.EXPENSES.accentColor
            )
        }

        if (data.repeatItems.isNotEmpty() && height > 60.dp) {
            RepeatHistoryList(
                repeatItems = data.repeatItems,
                availableHeight = height - 40.dp
            )
        }
    }
}

@Composable
private fun AmountText(
    amount: Long,
    prefix: String,
    color: Color,
) {
    WMText(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp),
        text = "$prefix${amount.formatWithCommas()}",
        style = MaterialTheme.typography.labelSmall,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(
            minFontSize = 9.sp,
            maxFontSize = 11.sp,
            stepSize = 1.sp
        )
    )
}

@Composable
private fun RepeatHistoryList(
    repeatItems: List<HistoryVo>,
    availableHeight: Dp,
) {
    val itemHeight = 14.dp
    val maxVisibleItems = (availableHeight / itemHeight).toInt().coerceAtMost(repeatItems.size)

    if (maxVisibleItems > 0) {
        Column {
            repeatItems.take(maxVisibleItems).forEach { item ->
                val content = item.content.default().ifEmpty { item.categoryInfo }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
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
                        text = content,
                        style = MaterialTheme.typography.labelSmall,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    text = "...",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayGridPreview() {
    val testDate = today
    val history = listOf(
        HistoryVo(
            id = "1",
            largeCategory = LargeCategoryEnum.INCOME,
            date = testDate,
            amount = 50000L,
            content = "급여"
        ),
        HistoryVo(
            id = "2",
            largeCategory = LargeCategoryEnum.EXPENSES,
            date = testDate,
            amount = 12000L,
            content = "점심"
        ),
        HistoryVo(
            id = "3",
            largeCategory = LargeCategoryEnum.EXPENSES,
            date = testDate,
            amount = 4500L,
            content = "커피"
        )
    )

    WMTheme {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            DayGrid(
                selectedDate = testDate,
                selectedMonth = testDate,
                historyItems = history,
                dayNormalHeight = 400.dp,
                dayMaxHeight = 600.dp,
                expansionProgress = 0f,
                collapseProgress = 0f,
                filterOptions = CalendarFilterOption.entries.toSet(),
                onClickDate = {}
            )
        }
    }
}
