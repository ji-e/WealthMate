package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
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
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin
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
    onClickDate: (LocalDate) -> Unit,
) {
    val items = remember(selectedMonth) {
        mutableListOf<Pair<LocalDate, MonthPeriodEnum>>().apply {
            val firstDay = selectedMonth.firstDayOfMonth()
            val startPadding = firstDay.dayOfWeek.isoDayNumber % 7

            // 이전 달
            repeat(startPadding) {
                add(
                    firstDay.minus(
                        startPadding - it,
                        DateTimeUnit.DAY
                    ) to MonthPeriodEnum.LAST_MONTH
                )
            }

            // 이번 달
            repeat(selectedMonth.lastDayOfMonth().day) {
                add(firstDay.plus(it, DateTimeUnit.DAY) to MonthPeriodEnum.THIS_MONTH)
            }

            // 다음 달
            val itemsSize = this.size
            if (itemsSize < 35) {
                repeat(35 - this.size) {
                    add(
                        firstDay.lastDayOfMonth()
                            .plus(it + 1, DateTimeUnit.DAY) to MonthPeriodEnum.NEXT_MONTH
                    )
                }
            } else if (itemsSize > 35) {
                repeat(42 - this.size) {
                    add(
                        firstDay.lastDayOfMonth()
                            .plus(it + 1, DateTimeUnit.DAY) to MonthPeriodEnum.NEXT_MONTH
                    )
                }
            }
        }
    }

    val numRowsForCurrentMonth = ceil(items.size / 7f)

    // 선택된 날짜가 몇 번째 주에 속하는지 계산
    val selectedItemIndex = remember(items, selectedDate) {
        items.indexOfFirst { it.first == selectedDate }
    }
    val selectedRowIndex = remember(selectedItemIndex) {
        if (selectedItemIndex < 0) 0 else selectedItemIndex / 7
    }


    if (items.isEmpty().not()) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(horizontal = 4.dp),
            userScrollEnabled = false
        ) {
            items(items.size) { index ->
                val history = historyItems.filter { it.date == items[index].first }
                val fixedItems = history.filter { it.category?.isFixed.default() }
                val incomeAmount = history
                    .filter { it.largeCategory == LargeCategoryEnum.INCOME }
                    .sumOf { it.amount }
                val expenseAmount = history
                    .filter { it.largeCategory == LargeCategoryEnum.EXPENSES }
                    .sumOf { it.amount }
                    .minus(fixedItems.sumOf { it.amount })

                val day = items[index]
                val rowIndex = index / 7

                DayItem(
                    day = day,
                    isSelected = day.first == selectedDate,
                    isInSelectedWeek = rowIndex == selectedRowIndex,
                    incomeAmount = incomeAmount,
                    expenseAmount = expenseAmount,
                    fixedItems = fixedItems,
                    dayNormalHeight = dayNormalHeight / numRowsForCurrentMonth,
                    dayMaxHeight = dayMaxHeight / numRowsForCurrentMonth,
                    expansionProgress = expansionProgress,
                    collapseProgress = collapseProgress,
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
    isInSelectedWeek: Boolean,
    incomeAmount: Long,
    expenseAmount: Long,
    fixedItems: List<HistoryVo?>,
    dayNormalHeight: Dp,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    onClickDate: (LocalDate) -> Unit,
) {
    // 확장/축소 진행률에 따라 높이와 투명도를 계산
    val height = when {
        // 1. 축소 중 (Normal -> Minimized)
        collapseProgress > 0f -> {
            val minHeight = if (isInSelectedWeek) 72.dp else 0.dp
            lerp(start = dayNormalHeight, stop = minHeight, fraction = collapseProgress)
        }
        // 2. 확장 중 (Normal -> Maximized)
        expansionProgress > 0f -> {
            lerp(start = dayNormalHeight, stop = dayMaxHeight, fraction = expansionProgress)
        }
        // 3. 기본 상태 (Normal)
        else -> dayNormalHeight
    }

    val alpha = when {
        // 축소 중에만 선택되지 않은 주의 투명도를 조절
        collapseProgress > 0f -> {
            if (isInSelectedWeek) 1f
            else (1 - collapseProgress).coerceIn(0f, 1f)
        }

        else -> 1f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
            .clickable { onClickDate(day.first) }
            .padding(horizontal = 3.dp, vertical = 2.dp),

        ) {
        val isToday = day.first == today
        val dayColor = when {
            isToday -> ColorGray.White
            day.second == MonthPeriodEnum.LAST_MONTH || day.second == MonthPeriodEnum.NEXT_MONTH -> ColorGray.Gray_200
            else -> WeekEnum.creator(day.first.dayOfWeek.isoDayNumber).color
        }
        val dayBackgroundColor = when {
            isToday -> WeekEnum.creator(day.first.dayOfWeek.isoDayNumber).color
            isSelected -> ColorPrimary.Primary_200
            else -> ColorGray.White
        }

        WMText(
            modifier = Modifier
                .alpha(alpha)
                .padding(bottom = 2.dp)
                .background(color = dayBackgroundColor, shape = CircleShape)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 8.dp),
            text = day.first.day.toString(),
            style = Typography().bodySmall.copy(
                color = dayColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )

        if (incomeAmount > 0) {
            WMText(
                modifier = Modifier.alpha(alpha).fillMaxWidth(),
                text = "+${formatWithCommas(incomeAmount.toString())}",
                style = Typography().labelSmall.copy(color = ColorBlue.Blue_300),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        if (expenseAmount > 0) {
            WMText(
                modifier = Modifier.alpha(alpha).fillMaxWidth(),
                text = "-${formatWithCommas(expenseAmount.toString())}",
                style = Typography().labelSmall.copy(color = ColorRed.Red_300),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        // 고정 아이템 영역
        if (fixedItems.isNotEmpty()) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha = alpha),
            ) {
                val itemHeight = 14.dp
                val maxVisibleItems = (maxHeight / itemHeight).toInt()
                val isOverflow = fixedItems.size > maxVisibleItems

                Column {
                    val itemsToShow =
                        if (isOverflow) {
                            fixedItems.take(
                                (maxVisibleItems - 1).coerceAtLeast(minimumValue = 0)
                            )
                        } else {
                            fixedItems
                        }

                    itemsToShow.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = itemHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(resource = Res.drawable.ic_push_pin),
                                contentDescription = null,
                                tint = ColorRed.Red_300,
                                modifier = Modifier.size(size = 12.dp)
                            )

                            WMText(
                                text = item?.content.default(),
                                style = Typography().labelSmall,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }

                    // 높이를 초과할 경우 "..." 표시
                    if (isOverflow && maxVisibleItems > 0) {
                        WMText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = itemHeight),
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
