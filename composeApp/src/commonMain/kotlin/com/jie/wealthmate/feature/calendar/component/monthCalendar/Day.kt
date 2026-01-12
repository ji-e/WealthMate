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
import kotlinx.datetime.plus


@Composable
fun DayGrid(
    today: LocalDate,
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    onClickDate: (LocalDate) -> Unit,
) {
    val items = remember(selectedMonth) {
        mutableListOf<LocalDate?>().apply {
            val firstDay = selectedMonth.firstDayOfMonth()
            val startPadding = (firstDay.dayOfWeek.isoDayNumber - 1) % 7
            repeat(startPadding) {
                add(null)
            }

            add(firstDay)

            repeat(selectedMonth.lastDayOfMonth().day - 1) {
                add(firstDay.plus(it + 1, DateTimeUnit.DAY))
            }
        }
    }

    // 선택된 날짜가 몇 번째 주에 속하는지 계산
    val selectedItemIndex = remember(items, selectedDate) {
        items.indexOf(selectedDate)
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
                    isSelected = day == selectedDate,
                    isInSelectedWeek = rowIndex == selectedRowIndex,
                    dayMaxHeight = dayMaxHeight,
                    expansionProgress = expansionProgress,
                    collapseProgress = collapseProgress,
                    onClickDate = {
                        day?.let { onClickDate(it) }
                    }
                )
            }
        }
    }
}

@Composable
internal fun DayItem(
    modifier: Modifier = Modifier,
    day: LocalDate?,
    today: LocalDate,
    isSelected: Boolean,
    isInSelectedWeek: Boolean,
    dayMaxHeight: Dp,
    expansionProgress: Float,
    collapseProgress: Float,
    onClickDate: (LocalDate) -> Unit,
) {
    // 확장/축소 진행률에 따라 높이와 투명도를 계산
    val height = when {
        // 1. 축소 중 (Normal -> Minimized)
        collapseProgress > 0f -> {
            val minHeight = if (isInSelectedWeek) 44.dp else 0.dp
            lerp(start = 44.dp, stop = minHeight, fraction = collapseProgress)
        }
        // 2. 확장 중 (Normal -> Maximized)
        expansionProgress > 0f -> {
            lerp(start = 44.dp, stop = dayMaxHeight, fraction = expansionProgress)
        }
        // 3. 기본 상태 (Normal)
        else -> 44.dp
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
            .clickable { day?.let { onClickDate(it) } },
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            WMText(
                modifier = Modifier.alpha(alpha),
                text = day.day.toString(),
                color = WeekEnum.creator(day.dayOfWeek.isoDayNumber).color,
                textAlign = TextAlign.Center
            )
        }
    }
}
