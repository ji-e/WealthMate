package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate


class CalendarScreenModel : BaseScreenModel<CalendarUiState>() {
    override val initialState: CalendarUiState
        get() = CalendarUiState()

    fun updateSelectedMonth(month: LocalDate = today) {
        reduceState { state ->
            state.copy(selectedMonth = month)
        }

        if (month.month == today.month) {
            updateSelectedDate(today)
        } else {
            updateSelectedDate(month.firstDayOfMonth())
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        reduceState { state ->
            state.copy(selectedDate = date)
        }
    }

}