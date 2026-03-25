package com.jie.wealthmate.feature.home.categoryExpenses

import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.base.DAY_END_MILLIS_OFFSET
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.component.vo.PeriodsVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.utils.convertDateToLocalDate
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.firstDayOfMonth
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CategoryExpensesViewModel(
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    private val selectedDate: String?,
    private val initialStatusType: StatusType,
    private val initialLargeCategory: LargeCategoryEnum,
    private val categoryId: String?
) : BaseViewModel<CategoryExpensesUiState>() {

    override val initialState: CategoryExpensesUiState = CategoryExpensesUiState(
        statusType = initialStatusType,
        largeCategory = initialLargeCategory,
        selectedMonth = selectedDate.convertDateToLocalDate(),
        isHome = selectedDate.isNullOrBlank()
    )

    private val filterFlow = MutableStateFlow(initialStatusType to initialLargeCategory)
    private val selectedMonthFlow = MutableStateFlow(today)

    init {
        Napier.e("selectedDate: $selectedDate")
        Napier.e("initialStatusType: $initialStatusType")
        Napier.e("initialLargeCategory: $initialLargeCategory")
        Napier.e("categoryId: $categoryId")

        combine(filterFlow, selectedMonthFlow) { filter, month ->
            filter to month
        }.onEach {
            loadHistories(isRefresh = true)
        }.launchIn(viewModelScope)
    }

    /**
     * 기간 타입을 업데이트합니다.
     */
    fun updateStatusType(statusType: StatusType) {
        if (container.uiState.value.statusType == statusType) return
        filterFlow.value = statusType to filterFlow.value.second
    }

    /**
     * 선택된 월을 업데이트합니다.
     */
    fun updateSelectedMonth(month: LocalDate) {
        if (container.uiState.value.selectedMonth == month) return
        selectedMonthFlow.value = month
    }

    /**
     * 다음 페이지 데이터를 로드합니다.
     */
    fun loadNextPage() {
        loadHistories(isRefresh = false)
    }

    /**
     * 내역 데이터를 로드합니다.
     * @param isRefresh true면 첫 페이지부터 다시 로드, false면 다음 페이지 로드
     */
    private fun loadHistories(isRefresh: Boolean) {
        val currentState = container.uiState.value
        if (!isRefresh && (currentState.isPagingLoading || currentState.isLastPage)) return

        val (statusType, largeCategory) = filterFlow.value
        val selectedMonth = selectedMonthFlow.value
        val currentPage = if (isRefresh) 0 else currentState.page
        val limit = 20

        flow {
            val isUnsetSearch = categoryId.isNullOrBlank() || categoryId.startsWith(CategoryVo.UNSET_ID_PREFIX)
            val searchCategoryIds = if (isUnsetSearch) listOf(CategoryVo.UNSET_ID_PREFIX) else listOfNotNull(categoryId)
            
            // 홈 여부에 따른 기간 설정
            val periods = if (currentState.isHome) {
                getPeriods(statusType)
            } else {
                getPeriodsByMonth(selectedMonth)
            }

            // 1. 내역 페이징 조회
            val histories = historyRepository.searchHistories(
                query = "",
                sortOrder = "LATEST",
                startDate = periods.currentStart,
                endDate = periods.currentEnd,
                largeCategories = listOf(largeCategory.name),
                categoryIds = searchCategoryIds,
                paymentMethodIds = emptyList(),
                limit = limit,
                offset = currentPage * limit
            )

            // 2. 리프레시인 경우에만 요약 정보 및 카테고리 정보 조회
            val summary = if (isRefresh) {
                val currentSum = historyRepository.getSearchSummary(
                    query = "",
                    startDate = periods.currentStart,
                    endDate = periods.currentEnd,
                    largeCategories = listOf(largeCategory.name),
                    categoryIds = searchCategoryIds,
                    paymentMethodIds = emptyList()
                )[largeCategory.name].default()

                val lastSum = historyRepository.getSearchSummary(
                    query = "",
                    startDate = periods.lastStart,
                    endDate = periods.lastEnd,
                    largeCategories = listOf(largeCategory.name),
                    categoryIds = searchCategoryIds,
                    paymentMethodIds = emptyList()
                )[largeCategory.name].default()

                val categoryVo = if (!isUnsetSearch && categoryId != null) {
                    categoryRepository.getCategoryById(categoryId)?.mapperToVo(largeCategory)
                } else {
                    CategoryVo.unset(largeCategory)
                }
                Triple(currentSum, lastSum, categoryVo)
            } else null

            emit(histories to summary)
        }.onStart {
            reduceState { it.copy(isPagingLoading = true) }
        }.apiFlow(
            showLoadingIndicator = isRefresh && currentPage == 0,
            errorFunc = { e ->
                reduceState { it.copy(isPagingLoading = false) }
                showSnackbar(e.message ?: "데이터를 불러오는 중 오류가 발생했습니다.")
            }
        ) { (newHistories, summary) ->
            reduceState { state ->
                state.copy(
                    statusType = statusType,
                    selectedMonth = selectedMonth,
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
        }
    }

    /**
     * 기간 타입(주/월/년)에 맞춰 현재 및 비교 대상 기간을 계산합니다.
     */
    private fun getPeriods(statusType: StatusType): PeriodsVo {
        val (currentRange, lastRange) = when (statusType) {
            StatusType.WEEK -> {
                val start = today.minus(today.dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
                (start to start.plus(6, DateTimeUnit.DAY)) to
                        (start.minus(7, DateTimeUnit.DAY) to start.minus(1, DateTimeUnit.DAY))
            }

            StatusType.MONTH -> {
                val lastMonth = today.minus(1, DateTimeUnit.MONTH)
                (today.firstDayOfMonth() to today.lastDayOfMonth()) to
                        (lastMonth.firstDayOfMonth() to lastMonth.lastDayOfMonth())
            }

            StatusType.YEAR -> {
                (LocalDate(today.year, 1, 1) to LocalDate(today.year, 12, 31)) to
                        (LocalDate(today.year - 1, 1, 1) to LocalDate(today.year - 1, 12, 31))
            }
        }

        return PeriodsVo(
            currentStart = currentRange.first.toEpochMilliseconds(),
            currentEnd = currentRange.second.toEpochMilliseconds() + DAY_END_MILLIS_OFFSET,
            lastStart = lastRange.first.toEpochMilliseconds(),
            lastEnd = lastRange.second.toEpochMilliseconds() + DAY_END_MILLIS_OFFSET
        )
    }

    /**
     * 선택된 월을 기준으로 현재 월 및 이전 월 기간을 계산합니다.
     */
    private fun getPeriodsByMonth(month: LocalDate): PeriodsVo {
        val currentStart = month.firstDayOfMonth()
        val currentEnd = month.lastDayOfMonth()
        val lastMonth = month.minus(1, DateTimeUnit.MONTH)
        val lastStart = lastMonth.firstDayOfMonth()
        val lastEnd = lastMonth.lastDayOfMonth()

        return PeriodsVo(
            currentStart = currentStart.toEpochMilliseconds(),
            currentEnd = currentEnd.toEpochMilliseconds() + DAY_END_MILLIS_OFFSET,
            lastStart = lastStart.toEpochMilliseconds(),
            lastEnd = lastStart.toEpochMilliseconds() + DAY_END_MILLIS_OFFSET
        )
    }
}
