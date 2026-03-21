package com.jie.wealthmate.feature.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.HistoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SearchViewModel(
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseViewModel<SearchUiState>() {

    override val initialState: SearchUiState = SearchUiState()

    private val PAGE_SIZE = 20
    private var searchJob: Job? = null
    private var debounceJob: Job? = null

    init {
        loadInitialData()
        search(isFirstPage = true)
    }

    private fun loadInitialData() {
        getAllCategories()
        getAllPaymentMethods()
    }

    private fun getAllCategories() {
        categoryRepository.getAllCategories().apiFlow(showLoadingIndicator = false) { response ->
            val categories = response.map { it.mapperToVo() }
                .sortedWith(
                    compareBy<CategoryVo> {
                        when (it.largeCategory) {
                            LargeCategoryEnum.INCOME -> 0
                            LargeCategoryEnum.SAVING -> 1
                            LargeCategoryEnum.EXPENSES -> 2
                        }
                    }.thenBy { it.sort }
                ).toImmutableList()
            reduceState { it.copy(categories = categories) }
        }
    }

    private fun getAllPaymentMethods() {
        paymentMethodRepository.getPaymentMethods().apiFlow(showLoadingIndicator = false) { response ->
            val paymentMethods =
                (response.map { it.paymentMethod.mapperToVo() } + listOf(PaymentMethodVo.UNSET)).toImmutableList()
            reduceState { it.copy(paymentMethods = paymentMethods) }
        }
    }

    fun updateQuery(query: TextFieldValue) {
        reduceState { it.copy(query = query) }
        
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(500)
            search(isFirstPage = true)
        }
    }

    fun clearQuery() {
        debounceJob?.cancel()
        reduceState {
            it.copy(
                query = TextFieldValue(""),
                searchResults = persistentListOf(),
                summary = persistentMapOf(),
                offset = 0,
                hasMore = true
            )
        }
        search(isFirstPage = true)
    }

    fun search(isFirstPage: Boolean = true) {
        val currentState = container.uiState.value
        if (currentState.isLoading || (!isFirstPage && !currentState.hasMore)) return

        searchJob?.cancel()
        searchJob = ioScope.launch {
            try {
                reduceState { it.copy(isLoading = true) }
                
                val offset = if (isFirstPage) 0 else currentState.offset

                val summary = if (isFirstPage) {
                    historyRepository.getSearchSummary(
                        query = currentState.query.text,
                        startDate = currentState.startDate?.toEpochMilliseconds(),
                        endDate = currentState.endDate?.toEpochMilliseconds(),
                        largeCategories = currentState.selectedLargeCategories.map { it.name },
                        categoryIds = currentState.selectedCategories.map { it.id },
                        paymentMethodIds = currentState.selectedPaymentMethods.map { it.id }
                    ).mapKeys { LargeCategoryEnum.creator(it.key) }.toImmutableMap()
                } else null

                val results = historyRepository.searchHistories(
                    query = currentState.query.text,
                    sortOrder = currentState.sortOrder.name,
                    startDate = currentState.startDate?.toEpochMilliseconds(),
                    endDate = currentState.endDate?.toEpochMilliseconds(),
                    largeCategories = currentState.selectedLargeCategories.map { it.name },
                    categoryIds = currentState.selectedCategories.map { it.id },
                    paymentMethodIds = currentState.selectedPaymentMethods.map { it.id },
                    limit = PAGE_SIZE,
                    offset = offset
                )

                reduceState { state ->
                    val mappedResults = results.map { it.mapperToVo() }
                    state.copy(
                        searchResults = if (isFirstPage) mappedResults.toImmutableList() else (state.searchResults + mappedResults).toImmutableList(),
                        summary = summary ?: state.summary,
                        isLoading = false,
                        hasMore = results.size == PAGE_SIZE,
                        offset = (if (isFirstPage) 0 else state.offset) + results.size
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                reduceState { it.copy(isLoading = false) }
                showSnackbar(e.message ?: "검색 중 오류가 발생했습니다.")
            }
        }
    }

    fun loadMore() {
        search(isFirstPage = false)
    }

    fun updateSortOrder(searchSortOrder: SearchSortOrder) {
        reduceState { it.copy(sortOrder = searchSortOrder) }
        search(isFirstPage = true)
    }

    fun updateDateRange(startDate: LocalDate?, endDate: LocalDate?) {
        val finalEndDate = if (startDate != null && endDate == null) startDate else endDate
        reduceState { it.copy(startDate = startDate, endDate = finalEndDate) }
        search(isFirstPage = true)
    }

    fun updateLargeCategories(largeCategories: List<LargeCategoryEnum>) {
        reduceState { state ->
            state.copy(
                selectedLargeCategories = largeCategories,
                selectedCategories = emptyList()
            )
        }
        search(isFirstPage = true)
    }

    fun updateCategories(categories: List<CategoryVo>) {
        reduceState { state ->
            val relevantLargeCategories = if (categories.isEmpty()) {
                state.selectedLargeCategories
            } else {
                categories.map { it.largeCategory }.distinct()
            }

            state.copy(
                selectedCategories = categories,
                selectedLargeCategories = relevantLargeCategories
            )
        }
        search(isFirstPage = true)
    }

    fun updatePaymentMethods(paymentMethods: List<PaymentMethodVo>) {
        reduceState { it.copy(selectedPaymentMethods = paymentMethods) }
        search(isFirstPage = true)
    }

    fun resetFilters() {
        reduceState {
            it.copy(
                selectedLargeCategories = emptyList(),
                selectedCategories = emptyList(),
                selectedPaymentMethods = emptyList(),
                startDate = null,
                endDate = null,
                sortOrder = SearchSortOrder.LATEST,
                query = TextFieldValue(""),
                summary = persistentMapOf(),
                offset = 0,
                hasMore = true
            )
        }
        search(isFirstPage = true)
    }
}
