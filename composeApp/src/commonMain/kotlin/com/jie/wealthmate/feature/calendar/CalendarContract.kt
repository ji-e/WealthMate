package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val selectedMonth: LocalDate = today,
    val selectedDate: LocalDate = today,
    val histories: List<HistoryVo> = emptyList(),
) : BaseUiState