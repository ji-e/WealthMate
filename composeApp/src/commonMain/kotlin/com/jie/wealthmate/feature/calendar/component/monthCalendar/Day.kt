package com.jie.wealthmate.feature.calendar.component.monthCalendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus

@Composable
fun GridDay(
    modifier: Modifier = Modifier,
    today: LocalDate,
    selectedDate: LocalDate,
    selectedMonth: LocalDate,
    onClickDate: (LocalDate) -> Unit,
) {

    val items = mutableListOf<LocalDate?>().apply {
        val firstDay = selectedMonth.firstDayOfMonth()

        repeat(firstDay.dayOfWeek.isoDayNumber) {
            add(null)
        }

        add(firstDay)

        repeat(selectedMonth.lastDayOfMonth().day - 1) {
            add(firstDay.plus(it + 1, DateTimeUnit.DAY))
        }
    }

    if (items.isEmpty()) return

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(items.size) { index ->
            val day = items[index]
            Day(
                day = day,
                today = today,
                isSelected = day == selectedDate
            ) {
                day?.let { onClickDate(it) }
            }
        }
    }
}

@Composable
private fun Day(
    day: LocalDate?,
    today: LocalDate,
    isSelected: Boolean,
    onClickDate: (LocalDate) -> Unit,
) {
    day?.let {
        WMText(
            text = it.day.toString(),
            color = WeekEnum.creator(day.dayOfWeek.isoDayNumber).color,
            textAlign = TextAlign.Center
        )
    }
}