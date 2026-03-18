package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.MainUiManager
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
import kotlinx.coroutines.flow.first
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
        get() = CalendarUiState(
            selectedDate = MainUiManager.uiState.value.selectedDate,
            selectedMonth = MainUiManager.uiState.value.selectedDate
        )

    init {
        getHistoriesByMonth()
        observeRepeatCycles()
    }

    fun updateSelectedMonth(month: LocalDate = today) {
        reduceState { state ->
            val currentDayOfMonth = state.selectedDate.day
            val lastDayOfNewMonth = month.lastDayOfMonth()
            val newDay = currentDayOfMonth.coerceAtMost(lastDayOfNewMonth.day)
            val newSelectedDate = if (month == today) today else LocalDate(month.year, month.month, newDay)

            MainUiManager.updateSelectedDate(newSelectedDate)

            state.copy(
                selectedMonth = month,
                selectedDate = newSelectedDate
            )
        }
        getHistoriesByMonth()
    }

    fun updateSelectedDate(date: LocalDate) {
        MainUiManager.updateSelectedDate(date)
        reduceState { state ->
            state.copy(selectedDate = date)
        }
    }

    fun updateFilterOptions(options: Set<CalendarFilterOption>) {
        reduceState { state ->
            state.copy(filterOptions = options)
        }
    }

    fun getHistoriesByMonth() {
        val selectedMonth = container.uiState.value.selectedMonth
        historyRepository.getHistoriesByMonth(
            startDate = selectedMonth.firstDayOfMonth().toEpochMilliseconds(),
            endDate = selectedMonth.lastDayOfMonth().toEpochMilliseconds()
        ).apiFlow { response ->
            reduceState { state ->
                state.copy(histories = response.map { it.mapperToVo() })
            }
        }
    }

    private fun observeRepeatCycles() {
        repeatCycleRepository.getRepeatCycleWithDetails()
            .apiFlow { response ->
                checkAndCreateRepeatCycleHistories(response)
            }
    }

    private var isCreatingRepeatHistories = false

    private suspend fun checkAndCreateRepeatCycleHistories(repeatCycles: List<RepeatCycleWithDetails>) {
        if (isCreatingRepeatHistories) return
        isCreatingRepeatHistories = true

        try {
            val selectedMonth = container.uiState.value.selectedMonth
            // 오늘이 포함된 달에 대해서만 반복 내역 생성 처리
            if (selectedMonth.year != today.year || selectedMonth.month.number != today.month.number) {
                return
            }

            val histories = historyRepository.getHistoriesByMonth(
                startDate = selectedMonth.firstDayOfMonth().toEpochMilliseconds(),
                endDate = selectedMonth.lastDayOfMonth().toEpochMilliseconds()
            ).first()

            val activeRepeatCycles = repeatCycles.filter { it.repeatCycle.isActive }
            if (activeRepeatCycles.isEmpty()) return

            val newHistories = mutableListOf<HistoryEntity>()

            for (repeatCycleDetail in activeRepeatCycles) {
                val repeatCycle = repeatCycleDetail.repeatCycle

                // 수정된 데이터이고, 수정된 날짜가 현재 선택된 달과 같으면 이번 달은 반영하지 않음 (다음 달부터 반영)
                val updatedDate = repeatCycle.updatedAt.toLocalDate()
                if (repeatCycle.isModified &&
                    updatedDate.year == selectedMonth.year &&
                    updatedDate.month == selectedMonth.month
                ) {
                    continue
                }

                val generationDates = getGenerationDatesForMonth(repeatCycle, selectedMonth)

                for (date in generationDates) {
                    val alreadyExists = histories.any {
                        it.history.repeatCycleId == repeatCycle.id && it.history.date.toLocalDate() == date
                    } || newHistories.any {
                        it.repeatCycleId == repeatCycle.id && it.date.toLocalDate() == date
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
                historyRepository.insertHistories(newHistories)
                // 삽입 후 UI 갱신을 위해 다시 조회
                getHistoriesByMonth()
            }
        } finally {
            isCreatingRepeatHistories = false
        }
    }

    private fun getGenerationDatesForMonth(
        repeatCycle: RepeatCycleEntity,
        month: LocalDate,
    ): List<LocalDate> {
        val cycle = RepeatCycleEnum.create(repeatCycle.repeatCycle)
        val referenceDate = repeatCycle.date.toLocalDate()
        val startDate = repeatCycle.startDate.toLocalDate()
        val endDate = repeatCycle.endDate?.toLocalDate()

        val monthStart = month.firstDayOfMonth()
        val monthEnd = month.lastDayOfMonth()

        // 실제 처리해야 할 기간 설정
        val start = if (startDate > monthStart) startDate else monthStart
        val end = if (endDate != null && endDate < monthEnd) endDate else monthEnd

        if (start > end) return emptyList()

        val dates = mutableListOf<LocalDate>()
        var currentDate = start

        while (currentDate <= end) {
            val shouldAdd = when (cycle) {
                RepeatCycleEnum.DAILY -> true
                RepeatCycleEnum.WEEKDAY -> currentDate.dayOfWeek.isoDayNumber in 1..5
                RepeatCycleEnum.WEEKEND -> currentDate.dayOfWeek.isoDayNumber in 6..7
                RepeatCycleEnum.WEEKLY -> currentDate.dayOfWeek.isoDayNumber == repeatCycle.dayOfWeek
                RepeatCycleEnum.MONTHLY -> currentDate.day == repeatCycle.dayOfMonth
                RepeatCycleEnum.MONTH_END -> currentDate == currentDate.lastDayOfMonth()
                RepeatCycleEnum.YEARLY -> currentDate.month.number == referenceDate.month.number && currentDate.day == referenceDate.day
                else -> false
            }
            if (shouldAdd) dates.add(currentDate)
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        return dates
    }
}
