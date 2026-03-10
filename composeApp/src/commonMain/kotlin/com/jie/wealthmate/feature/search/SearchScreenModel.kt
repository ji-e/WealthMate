package com.jie.wealthmate.feature.search

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate

class SearchScreenModel(
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<SearchUiState>() {
    override val initialState: SearchUiState = SearchUiState()

    init {
        getAllCategories()
        getAllPaymentMethods()
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
        // TODO: Implement search logic
    }

    fun clearQuery() {
        reduceState { it.copy(query = TextFieldValue(""), searchResults = persistentListOf()) }
    }

    fun search() {
        // TODO: Implement search with current query
    }

    fun updateSortOrder(sortOrder: SearchSortOrder) {
        reduceState { it.copy(sortOrder = sortOrder) }
        search()
    }

    fun updateDateRange(startDate: LocalDate?, endDate: LocalDate?) {
        reduceState { it.copy(startDate = startDate, endDate = endDate) }
        search()
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
        search()
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
        search()
    }

    fun updatePaymentMethods(paymentMethods: List<PaymentMethodVo>) {
        reduceState { it.copy(selectedPaymentMethods = paymentMethods) }
        search()
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
        search()
    }
}
