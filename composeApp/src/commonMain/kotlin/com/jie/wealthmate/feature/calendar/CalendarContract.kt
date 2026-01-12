package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val selectedMonth: LocalDate = today,
    val selectedDate: LocalDate = today,
) : BaseUiState