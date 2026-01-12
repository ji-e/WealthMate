package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseScreenModel


class CalendarScreenModel : BaseScreenModel<CalendarUiState>() {
    override val initialState: CalendarUiState
        get() = CalendarUiState()

}