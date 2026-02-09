package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.database.eneity.RepeatCycleWithDetails
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo.Companion.mapperToVo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus


class CalendarScreenModel(
    private val historyRepository: HistoryRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
) : BaseScreenModel<CalendarUiState>() {
    override val initialState: CalendarUiState
        get() = CalendarUiState()

    init {
        observeRepeatCycles()
        getHistoriesByMonth()
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
        getHistoriesByMonth()
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
        ).apiFlow { response ->
            val historyVo = response.map { it.mapperToVo() }
            println("getHistoriesByMonth::: ${'$'}historyVo")
            reduceState { state ->
                state.copy(
                    histories = historyVo
                )
            }
        }
    }

    private fun observeRepeatCycles() {
        screenScope.launch {
            repeatCycleRepository.getRepeatCycleWithDetails().collectLatest { repeatCycles ->
                checkAndCreateRepeatCycleHistories(repeatCycles)
            }
        }
    }

    private suspend fun checkAndCreateRepeatCycleHistories(repeatCycles: List<RepeatCycleWithDetails>) {
        val selectedMonth = container.uiState.value.selectedMonth
        if (selectedMonth.year != today.year || selectedMonth.month.number != today.month.number) {
            return
        }

        val histories = historyRepository.getHistoriesByMonth(
            startDate = selectedMonth.firstDayOfMonth().toEpochMilliseconds(),
            endDate = selectedMonth.lastDayOfMonth().toEpochMilliseconds()
        ).first()

        val newHistories = mutableListOf<HistoryEntity>()

        for (repeatCycleDetail in repeatCycles.filter { it.repeatCycle.isActive }) {
            val repeatCycle = repeatCycleDetail.repeatCycle
            val generationDates = getGenerationDatesForMonth(repeatCycle, selectedMonth)

            for (date in generationDates) {
                val alreadyExists = histories.any {
                    it.history.repeatCycleId == repeatCycle.id && it.history.date.toLocalDate() == date
                }
                if (!alreadyExists) {
                    newHistories.add(
                        HistoryEntity(
                            largeCategory = repeatCycle.largeCategory,
                            date = date.toEpochMilliseconds(),
                            amount = repeatCycle.amount,
                            repeatCycleId = repeatCycle.id,
                            categoryId = repeatCycle.categoryId,
                            categoryTagId = repeatCycle.categoryTagId,
                            paymentMethodId = repeatCycle.paymentMethodId,
                            content = repeatCycle.content
                        )
                    )
                }
            }
        }

        if (newHistories.isNotEmpty()) {
            for (history in newHistories) {
                historyRepository.insertHistory(history)
            }
        }
    }

    private fun getGenerationDatesForMonth(
        repeatCycle: RepeatCycleEntity,
        month: LocalDate,
    ): List<LocalDate> {
        val cycle = RepeatCycleEnum.create(repeatCycle.repeatCycle)
        val startDate = repeatCycle.startDate.toLocalDate()
        val endDate = repeatCycle.endDate?.toLocalDate()

        val monthStart = month.firstDayOfMonth()
        val monthEnd = month.lastDayOfMonth()

        val dates = mutableListOf<LocalDate>()

        var currentDate = monthStart
        while (currentDate <= monthEnd) {
            if (currentDate >= startDate && (endDate == null || currentDate <= endDate)) {
                val shouldAdd = when (cycle) {
                    RepeatCycleEnum.DAILY -> true
                    RepeatCycleEnum.WEEKDAY -> currentDate.dayOfWeek.isoDayNumber in 1..5
                    RepeatCycleEnum.WEEKEND -> currentDate.dayOfWeek.isoDayNumber in 6..7
                    RepeatCycleEnum.WEEKLY -> currentDate.dayOfWeek.isoDayNumber == repeatCycle.dayOfWeek
                    RepeatCycleEnum.MONTHLY -> currentDate.day == repeatCycle.dayOfMonth
                    RepeatCycleEnum.MONTH_END -> currentDate == currentDate.lastDayOfMonth()
                    RepeatCycleEnum.YEARLY -> currentDate.month.number == startDate.month.number && currentDate.day == startDate.day
                    RepeatCycleEnum.UNKNOWN -> false
                }
                if (shouldAdd) {
                    dates.add(currentDate)
                }
            }
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        return dates
    }
}
