package com.jie.wealthmate.feature.home.categoryExpenses

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        filterFlow.onEach {
            loadHistories(isRefresh = true)
        }.launchIn(screenScope)
    }

    fun updateStatusType(statusType: StatusType) {
        if (container.uiState.value.statusType == statusType) return
        filterFlow.value = statusType to filterFlow.value.second
    }

    fun loadNextPage() {
        loadHistories(isRefresh = false)
    }

    private fun loadHistories(isRefresh: Boolean) {
        val (statusType, largeCategory) = filterFlow.value
        val periods = getPeriods(statusType)
        val isUnsetSearch = categoryId.isNullOrBlank() || categoryId.startsWith(CategoryVo.UNSET_ID_PREFIX)

        val currentState = container.uiState.value
        val currentPage = if (isRefresh) 0 else currentState.page
        val limit = 20

        if (!isRefresh && (currentState.isPagingLoading || currentState.isLastPage)) return

        reduceState { it.copy(isPagingLoading = true) }

        launchSafe(
            block = {
                val histories = historyRepository.searchHistories(
                    query = "",
                    sortOrder = "LATEST",
                    startDate = periods.currentStart,
                    endDate = periods.currentEnd,
                    largeCategories = listOf(largeCategory.name),
                    categoryIds = if (isUnsetSearch) listOf(CategoryVo.UNSET_ID_PREFIX) else listOfNotNull(categoryId),
                    paymentMethodIds = emptyList(),
                    limit = limit,
                    offset = currentPage * limit
                )

                val summary = if (isRefresh) {
                    val currentSum = historyRepository.getSearchSummary(
                        query = "",
                        startDate = periods.currentStart,
                        endDate = periods.currentEnd,
                        largeCategories = listOf(largeCategory.name),
                        categoryIds = if (isUnsetSearch) listOf(CategoryVo.UNSET_ID_PREFIX) else listOfNotNull(categoryId),
                        paymentMethodIds = emptyList()
                    )[largeCategory.name] ?: 0L

                    val lastSum = historyRepository.getSearchSummary(
                        query = "",
                        startDate = periods.lastStart,
                        endDate = periods.lastEnd,
                        largeCategories = listOf(largeCategory.name),
                        categoryIds = if (isUnsetSearch) listOf(CategoryVo.UNSET_ID_PREFIX) else listOfNotNull(categoryId),
                        paymentMethodIds = emptyList()
                    )[largeCategory.name] ?: 0L

                    val categoryVo = if (!isUnsetSearch && categoryId != null) {
                        categoryRepository.getCategoryById(categoryId)?.mapperToVo(largeCategory)
                    } else {
                        CategoryVo.unset(largeCategory)
                    }
                    Triple(currentSum, lastSum, categoryVo)
                } else null

                histories to summary
            },
            showLoading = isRefresh && currentPage == 0,
            onSuccess = { (newHistories, summary) ->
                reduceState { state ->
                    state.copy(
                        statusType = statusType,
                        largeCategory = largeCategory,
                        category = summary?.third ?: state.category,
                        totalAmount = summary?.first ?: state.totalAmount,
                        lastTotalAmount = summary?.second ?: state.lastTotalAmount,
                        histories = if (isRefresh) newHistories else state.histories + newHistories,
                        isPagingLoading = false,
                        isLastPage = newHistories.size < limit,
                        page = if (isRefresh) 1 else state.page + 1
                    )
                }
            },
            onError = {
                reduceState { it.copy(isPagingLoading = false) }
            }
        )
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
