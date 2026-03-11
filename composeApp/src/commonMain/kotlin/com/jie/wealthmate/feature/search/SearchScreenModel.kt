package com.jie.wealthmate.feature.search

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.base.BaseScreenModel
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SearchScreenModel(
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<SearchUiState>() {
    override val initialState: SearchUiState = SearchUiState()

    private val PAGE_SIZE = 20
    private var searchJob: Job? = null

    init {
        getAllCategories()
        getAllPaymentMethods()
        search(isFirstPage = true)
    }

    private fun getAllCategories() {
        categoryRepository.getAllCategories().apiFlow { response ->
            reduceState { state ->
                val categories = response.map { entity -> entity.mapperToVo() }
                    .sortedWith(
                        compareBy<CategoryVo> {
                            when (it.largeCategory) {
                                LargeCategoryEnum.INCOME -> 0
                                LargeCategoryEnum.SAVING -> 1
                                LargeCategoryEnum.EXPENSES -> 2
                            }
                        }.thenBy { it.sort }
                    ).toImmutableList()
                state.copy(
                    categories = categories,
                )
            }
        }
    }

    private fun getAllPaymentMethods() {
        paymentMethodRepository.getPaymentMethods().apiFlow { response ->
            reduceState { state ->
                val paymentMethods =
                    (response.map { it.paymentMethod.mapperToVo() } + listOf(PaymentMethodVo.UNSET)).toImmutableList()
                state.copy(
                    paymentMethods = paymentMethods,
                )
            }
        }
    }

    fun updateQuery(query: TextFieldValue) {
        reduceState { it.copy(query = query) }
        search(isFirstPage = true)
    }

    fun clearQuery() {
        reduceState { it.copy(query = TextFieldValue(""), searchResults = persistentListOf()) }
        search(isFirstPage = true)
    }

    fun search(isFirstPage: Boolean = true) {
        val currentState = container.uiState.value
        if (currentState.isLoading || (!isFirstPage && !currentState.hasMore)) return

        searchJob?.cancel()
        searchJob = screenModelScope.launch(Dispatchers.IO) {
            reduceState { it.copy(isLoading = true) }

            val offset = if (isFirstPage) 0 else currentState.offset

            try {
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
                ).map { it.mapperToVo() }

                reduceState { state ->
                    val newResults = if (isFirstPage) {
                        results.toImmutableList()
                    } else {
                        (state.searchResults + results).toImmutableList()
                    }
                    state.copy(
                        searchResults = newResults,
                        isLoading = false,
                        hasMore = results.size == PAGE_SIZE,
                        offset = offset + results.size
                    )
                }
            } catch (e: Exception) {
                reduceState { it.copy(isLoading = false) }
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
                query = TextFieldValue("")
            )
        }
        search(isFirstPage = true)
    }
}
