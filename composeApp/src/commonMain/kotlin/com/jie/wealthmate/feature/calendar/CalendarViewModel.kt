package com.jie.wealthmate.feature.calendar

import com.jie.wealthmate.MainUiManager
import com.jie.wealthmate.base.BaseViewModel
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
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus

class CalendarViewModel(
    private val historyRepository: HistoryRepository,
    private val repeatCycleRepository: RepeatCycleRepository,
    private val settings: Settings,
) : BaseViewModel<CalendarUiState>() {

    companion object {
        private const val KEY_CALENDAR_FILTER_OPTIONS = "calendar_filter_options"
    }

    override val initialState: CalendarUiState = run {
        // 로컬에 저장된 필터 옵션 로드
        val savedOptions = settings.getStringOrNull(KEY_CALENDAR_FILTER_OPTIONS)
        val initialFilters = savedOptions?.split(",")?.mapNotNull { name ->
            try {
                CalendarFilterOption.valueOf(name)
            } catch (e: Exception) {
                null
            }
        }?.toSet()
            ?: CalendarFilterOption.entries.toSet()

        CalendarUiState(
            selectedDate = MainUiManager.uiState.value.selectedDate,
            selectedMonth = MainUiManager.uiState.value.selectedDate.firstDayOfMonth(),
            filterOptions = initialFilters
        )
    }

    // 현재 선택된 월을 관리하는 Flow (데이터 로딩 트리거)
    private val selectedMonthFlow = MutableStateFlow(initialState.selectedMonth)

    init {
        observeCalendarData()
        observeRepeatCycles()
    }

    /**
     * 선택된 월에 따른 내역 데이터를 관찰합니다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCalendarData() {
        selectedMonthFlow.flatMapLatest { month ->
            historyRepository.getHistoriesByMonth(
                startDate = month.firstDayOfMonth().toEpochMilliseconds(),
                endDate = month.lastDayOfMonth().toEpochMilliseconds()
            )
        }.apiFlow { response ->
            reduceState { state ->
                state.copy(histories = response.map { it.mapperToVo() })
            }
            // DB Flow는 스트림이 유지되므로 첫 데이터 수신 시 로딩 해제
            showLoading(false)
        }
    }

    /**
     * 월을 변경하고 데이터를 갱신합니다.
     */
    fun updateSelectedMonth(month: LocalDate = today) {
        val currentDayOfMonth = container.uiState.value.selectedDate.day
        val lastDayOfNewMonth = month.lastDayOfMonth()
        val newDay = currentDayOfMonth.coerceAtMost(lastDayOfNewMonth.day)
        val newSelectedDate =
            if (month == today) today else LocalDate(month.year, month.month, newDay)

        MainUiManager.updateSelectedDate(newSelectedDate)

        // 트리거 업데이트
        selectedMonthFlow.value = month.firstDayOfMonth()

        reduceState { state ->
            state.copy(
                selectedMonth = month.firstDayOfMonth(),
                selectedDate = newSelectedDate
            )
        }
    }

    fun updateSelectedDate(date: LocalDate) {
        MainUiManager.updateSelectedDate(date)
        reduceState { state ->
            state.copy(selectedDate = date)
        }
    }

    fun updateFilterOptions(options: Set<CalendarFilterOption>) {
        settings[KEY_CALENDAR_FILTER_OPTIONS] = options.joinToString(",") { it.name }
        reduceState { state ->
            state.copy(filterOptions = options)
        }
    }

    private fun observeRepeatCycles() {
        repeatCycleRepository.getRepeatCycleWithDetails()
            .apiFlow(showLoadingIndicator = false) { response ->
                checkAndCreateRepeatCycleHistories(response)
            }
    }

    private var isCreatingRepeatHistories = false

    private suspend fun checkAndCreateRepeatCycleHistories(repeatCycles: List<RepeatCycleWithDetails>) {
        if (isCreatingRepeatHistories) return
        isCreatingRepeatHistories = true

        try {
            val selectedMonth = container.uiState.value.selectedMonth
            if (selectedMonth.year != today.year || selectedMonth.month.number != today.month.number) {
                return
            }

            // 삭제된 내역을 포함하여 이번 달의 모든 내역을 가져옵니다.
            // 이를 통해 사용자가 삭제한 반복 내역이 다시 생성되는 것을 방지합니다.
            val histories = historyRepository.getHistoriesByMonthWithDeleted(
                startDate = selectedMonth.firstDayOfMonth().toEpochMilliseconds(),
                endDate = selectedMonth.lastDayOfMonth().toEpochMilliseconds()
            )

            val activeRepeatCycles = repeatCycles.filter { it.repeatCycle.isActive }
            if (activeRepeatCycles.isEmpty()) return

            val newHistories = mutableListOf<HistoryEntity>()

            for (repeatCycleDetail in activeRepeatCycles) {
                val repeatCycle = repeatCycleDetail.repeatCycle
                val updatedDate = repeatCycle.updatedAt.toLocalDate()

                if (repeatCycle.isModified &&
                    updatedDate.year == selectedMonth.year &&
                    updatedDate.month == selectedMonth.month
                ) {
                    continue
                }

                val generationDates = getGenerationDatesForMonth(repeatCycle, selectedMonth)

                for (date in generationDates) {
                    // 삭제된 내역을 포함하여 이미 내역이 존재하는지 체크합니다.
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
