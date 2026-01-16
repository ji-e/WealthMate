package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.MainScreenModel
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate


class CalendarScreenModel(
    val mainScreenModel: MainScreenModel
) : BaseScreenModel<CalendarUiState>() {
    override val initialState: CalendarUiState
        get() = CalendarUiState()

    fun updateSelectedMonth(month: LocalDate = today) {
        reduceState { state ->
            val currentDayOfMonth = state.selectedDate.day
            val lastDayOfNewMonth = month.lastDayOfMonth()
            val newDay = currentDayOfMonth.coerceAtMost(lastDayOfNewMonth.day)
            val newSelectedDate = LocalDate(month.year, month.month, newDay)

            state.copy(
                selectedMonth = month,
                selectedDate = if (month == today) today else newSelectedDate
            )
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        reduceState { state ->
            state.copy(selectedDate = date)
        }
    }

}