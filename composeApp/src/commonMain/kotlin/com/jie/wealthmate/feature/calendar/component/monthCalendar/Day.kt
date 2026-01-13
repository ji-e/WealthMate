package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.math.ceil


@Composable
fun DayGrid(
    today: LocalDate,
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
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
                val day = items[index]
                val rowIndex = index / 7
                DayItem(
                    day = day,
                    today = today,
                    isSelected = day.first == selectedDate,
                    isInSelectedWeek = rowIndex == selectedRowIndex,
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
    today: LocalDate,
    isSelected: Boolean,
    isInSelectedWeek: Boolean,
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(if (isSelected) ColorBlue.Blue_50 else ColorGray.White)
            .clickable { onClickDate(day.first) },
        contentAlignment = Alignment.Center
    ) {

        WMText(
            modifier = Modifier.alpha(alpha),
            text = day.first.day.toString(),
            style = Typography().bodyMedium.copy(color = WeekEnum.creator(day.first.dayOfWeek.isoDayNumber).color),
            textAlign = TextAlign.Center
        )

    }
}
