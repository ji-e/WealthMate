package com.jie.wealthmate.feature.home.preparednessStatus

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class PreparednessStatusScreenModel(
    private val historyRepository: HistoryRepository,
    initialStatusType: StatusType,
    initialLargeCategory: LargeCategoryEnum
) : BaseScreenModel<PreparednessStatusUiState>() {

    override val initialState: PreparednessStatusUiState = PreparednessStatusUiState(
        statusType = initialStatusType,
        largeCategory = initialLargeCategory
    )

    private val filterFlow = MutableStateFlow(initialStatusType to initialLargeCategory)

    init {
        observeData()
    }

    fun updateStatusType(statusType: StatusType) {
        filterFlow.value = statusType to filterFlow.value.second
    }

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        filterFlow.value = filterFlow.value.first to largeCategory
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        filterFlow.flatMapLatest { (statusType, largeCategory) ->
            val periods = getPeriods(statusType)
            combine(
                historyRepository.getHistoriesByMonth(periods.currentStart, periods.currentEnd),
                historyRepository.getHistoriesByMonth(periods.lastStart, periods.lastEnd)
            ) { currentHistories, lastHistories ->
                val filteredCurrent = currentHistories.filter { it.history.largeCategory == largeCategory.name }
                val filteredLast = lastHistories.filter { it.history.largeCategory == largeCategory.name }

                PreparednessStatusUiState(
                    statusType = statusType,
                    largeCategory = largeCategory,
                    currentAmount = filteredCurrent.sumOf { it.history.amount },
                    lastAmount = filteredLast.sumOf { it.history.amount },
                    histories = filteredCurrent.sortedByDescending { it.history.date },
                    isLoading = false
                )
            }
        }.apiFlow { state ->
            reduceState { state }
        }
    }

    private fun getPeriods(statusType: StatusType): Periods {
        return when (statusType) {
            StatusType.WEEK -> {
                val currentStart = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
                val currentEnd = currentStart.plus(6, DateTimeUnit.DAY)
                val lastStart = currentStart.minus(7, DateTimeUnit.DAY)
                val lastEnd = lastStart.plus(6, DateTimeUnit.DAY)
                Periods(
                    currentStart.toEpochMilliseconds(),
                    currentEnd.toEpochMilliseconds() + 86_399_999L,
                    lastStart.toEpochMilliseconds(),
                    lastEnd.toEpochMilliseconds() + 86_399_999L
                )
            }
            StatusType.MONTH -> {
                val currentStart = today.firstDayOfMonth()
                val currentEnd = today.lastDayOfMonth()
                val lastMonth = today.minus(1, DateTimeUnit.MONTH)
                val lastStart = lastMonth.firstDayOfMonth()
                val lastEnd = lastMonth.lastDayOfMonth()
                Periods(
                    currentStart.toEpochMilliseconds(),
                    currentEnd.toEpochMilliseconds() + 86_399_999L,
                    lastStart.toEpochMilliseconds(),
                    lastEnd.toEpochMilliseconds() + 86_399_999L
                )
            }
            StatusType.YEAR -> {
                val currentStart = LocalDate(today.year, 1, 1)
                val currentEnd = LocalDate(today.year, 12, 31)
                val lastStart = LocalDate(today.year - 1, 1, 1)
                val lastEnd = LocalDate(today.year - 1, 12, 31)
                Periods(
                    currentStart.toEpochMilliseconds(),
                    currentEnd.toEpochMilliseconds() + 86_399_999L,
                    lastStart.toEpochMilliseconds(),
                    lastEnd.toEpochMilliseconds() + 86_399_999L
                )
            }
        }
    }

    data class Periods(
        val currentStart: Long,
        val currentEnd: Long,
        val lastStart: Long,
        val lastEnd: Long,
    )
}
