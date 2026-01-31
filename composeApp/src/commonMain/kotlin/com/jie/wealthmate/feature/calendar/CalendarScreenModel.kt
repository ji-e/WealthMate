package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate


class CalendarScreenModel(
    private val historyRepository: HistoryRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
) : BaseScreenModel<CalendarUiState>() {
    override val initialState: CalendarUiState
        get() = CalendarUiState()

    init {
        getHistoriesByMonth()
        getRepeatCycle()
    }

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

    fun getHistoriesByMonth() {
        val selectedMonth = container.uiState.value.selectedMonth
        historyRepository.getHistoriesByMonth(
            startDate = selectedMonth.firstDayOfMonth().toEpochMilliseconds(),
            endDate = selectedMonth.lastDayOfMonth().toEpochMilliseconds()
        ).apiFlow {
            println(it)
        }
    }

    fun getRepeatCycle() {
        repeatCycleRepository.getRepeatCycles().apiFlow {
            println(it)
        }
    }
}