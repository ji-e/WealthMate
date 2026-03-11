package com.jie.wealthmate.feature.search

import androidx.compose.ui.text.input.TextFieldValue
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SearchScreenModel(
    private val historyRepository: HistoryRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<SearchUiState>() {
    override val initialState: SearchUiState = SearchUiState()

    private val PAGE_SIZE = 20

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
                    )
                    .toImmutableList()
                state.copy(
                    categories = categories,
                    selectedCategories = categories
                )
            }
        }
    }

    private fun getAllPaymentMethods() {
        paymentMethodRepository.getPaymentMethods().apiFlow { response ->
            reduceState { state ->
                val paymentMethods = response.map { it.paymentMethod.mapperToVo() }.toImmutableList()
                state.copy(
                    paymentMethods = paymentMethods,
                    selectedPaymentMethods = paymentMethods
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

        reduceState { it.copy(isLoading = true) }

        val offset = if (isFirstPage) 0 else currentState.offset

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
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
        }
    }

    fun loadMore() {
        search(isFirstPage = false)
    }

    fun updateSortOrder(sortOrder: SearchSortOrder) {
        reduceState { it.copy(sortOrder = sortOrder) }
        search(isFirstPage = true)
    }

    fun updateDateRange(startDate: LocalDate?, endDate: LocalDate?) {
        reduceState { it.copy(startDate = startDate, endDate = endDate) }
        search(isFirstPage = true)
    }

    fun updateLargeCategories(largeCategories: List<LargeCategoryEnum>) {
        reduceState { state ->
            val updatedSelectedCategories = if (largeCategories.isNotEmpty()) {
                state.selectedCategories.filter { largeCategories.contains(it.largeCategory) }
            } else {
                state.selectedCategories
            }
            state.copy(
                selectedLargeCategories = largeCategories,
                selectedCategories = updatedSelectedCategories
            )
        }
        search(isFirstPage = true)
    }

    fun updateCategories(categories: List<CategoryVo>) {
        reduceState { state ->
            val updatedLargeCategories = categories.map { it.largeCategory }.distinct().let { largeCats ->
                if (largeCats.isNotEmpty()) {
                    (state.selectedLargeCategories + largeCats).distinct()
                } else {
                    state.selectedLargeCategories
                }
            }
            state.copy(
                selectedCategories = categories,
                selectedLargeCategories = updatedLargeCategories
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
                selectedLargeCategories = LargeCategoryEnum.entries,
                selectedCategories = it.categories,
                selectedPaymentMethods = it.paymentMethods,
                startDate = null,
                endDate = null,
                sortOrder = SearchSortOrder.LATEST
            )
        }
        search(isFirstPage = true)
    }
}
