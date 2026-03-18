package com.jie.wealthmate.feature.home.categoryExpenses

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CategoryExpensesScreenModel(
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    private val initialStatusType: StatusType,
    private val initialLargeCategory: LargeCategoryEnum,
    private val categoryId: String?
) : BaseScreenModel<CategoryExpensesUiState>() {

    override val initialState: CategoryExpensesUiState = CategoryExpensesUiState(
        statusType = initialStatusType,
        largeCategory = initialLargeCategory
    )

    private val filterFlow = MutableStateFlow(initialStatusType to initialLargeCategory)

    init {
        observeData()
    }

    fun updateStatusType(statusType: StatusType) {
        if (container.uiState.value.statusType == statusType) return
        filterFlow.value = statusType to filterFlow.value.second
    }

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        if (container.uiState.value.largeCategory == largeCategory) return
        filterFlow.value = filterFlow.value.first to largeCategory
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        filterFlow.flatMapLatest { (statusType, largeCategory) ->
            val periods = getPeriods(statusType)
            val isUnsetSearch = categoryId.isNullOrBlank() || categoryId.startsWith(CategoryVo.UNSET_ID_PREFIX)
            val effectiveCategoryId = if (isUnsetSearch) null else categoryId

            combine(
                historyRepository.getHistoriesByMonth(periods.currentStart, periods.currentEnd),
                historyRepository.getHistoriesByMonth(periods.lastStart, periods.lastEnd),
                if (effectiveCategoryId != null) categoryRepository.getCategoryByIdFlow(effectiveCategoryId) else flowOf(null)
            ) { currentHistories, lastHistories, categoryEntity ->
                val categoryVo = categoryEntity?.mapperToVo(largeCategory) ?: CategoryVo.unset(largeCategory)
                
                val filteredCurrent = currentHistories.filter { 
                    it.history.largeCategory == largeCategory.name && 
                    if (isUnsetSearch) it.history.categoryId.isNullOrBlank() else it.history.categoryId == effectiveCategoryId
                }
                val filteredLast = lastHistories.filter { 
                    it.history.largeCategory == largeCategory.name && 
                    if (isUnsetSearch) it.history.categoryId.isNullOrBlank() else it.history.categoryId == effectiveCategoryId
                }

                val histories = filteredCurrent.sortedByDescending { it.history.date }
                val groupedHistories = histories
                    .groupBy { it.history.date.toLocalDate() }
                    .toList()
                    .sortedByDescending { it.first }

                CategoryExpensesUiState(
                    statusType = statusType,
                    largeCategory = largeCategory,
                    category = categoryVo,
                    totalAmount = filteredCurrent.sumOf { it.history.amount },
                    lastTotalAmount = filteredLast.sumOf { it.history.amount },
                    histories = histories,
                    groupedHistories = groupedHistories
                )
            }
        }.apiFlow { newState ->
            reduceState { newState }
        }
    }

    private fun getPeriods(statusType: StatusType): Periods {
        val (currentRange, lastRange) = when (statusType) {
            StatusType.WEEK -> {
                val start = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
                (start to start.plus(6, DateTimeUnit.DAY)) to (start.minus(7, DateTimeUnit.DAY) to start.minus(1, DateTimeUnit.DAY))
            }
            StatusType.MONTH -> {
                val lastMonth = today.minus(1, DateTimeUnit.MONTH)
                (today.firstDayOfMonth() to today.lastDayOfMonth()) to (lastMonth.firstDayOfMonth() to lastMonth.lastDayOfMonth())
            }
            StatusType.YEAR -> {
                (LocalDate(today.year, 1, 1) to LocalDate(today.year, 12, 31)) to (LocalDate(today.year - 1, 1, 1) to LocalDate(today.year - 1, 12, 31))
            }
        }
        val endOffset = 86_399_999L
        return Periods(
            currentStart = currentRange.first.toEpochMilliseconds(),
            currentEnd = currentRange.second.toEpochMilliseconds() + endOffset,
            lastStart = lastRange.first.toEpochMilliseconds(),
            lastEnd = lastRange.second.toEpochMilliseconds() + endOffset
        )
    }

    data class Periods(
        val currentStart: Long,
        val currentEnd: Long,
        val lastStart: Long,
        val lastEnd: Long,
    )
}
