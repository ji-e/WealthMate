package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate


class CalendarScreenModel : BaseScreenModel<CalendarUiState>() {
    override val initialState: CalendarUiState
        get() = CalendarUiState()

    fun updateSelectedMonth(month: LocalDate = today) {
        reduceState { state ->
            state.copy(selectedMonth = month)
        }
    }

}