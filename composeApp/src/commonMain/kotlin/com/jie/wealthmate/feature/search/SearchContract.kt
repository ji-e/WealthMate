package com.jie.wealthmate.feature.search

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.HistoryVo
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

data class SearchUiState(
    val query: TextFieldValue = TextFieldValue(""),
    val sortOrder: SearchSortOrder = SearchSortOrder.LATEST,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val categories: ImmutableList<CategoryVo> = persistentListOf(),
    val paymentMethods: ImmutableList<PaymentMethodVo> = persistentListOf(),
    val selectedLargeCategories: List<LargeCategoryEnum> = emptyList(),
    val selectedCategories: List<CategoryVo> = emptyList(),
    val selectedPaymentMethods: List<PaymentMethodVo> = emptyList(),
    val searchResults: ImmutableList<HistoryVo> = persistentListOf(),
    val isLoading: Boolean = false,
    val hasMore: Boolean = true,
    val offset: Int = 0
) : BaseUiState {
    val isFilteredByType = selectedLargeCategories.isNotEmpty()
    val filteredCategories = if (isFilteredByType) {
        categories.filter { selectedLargeCategories.contains(it.largeCategory) }
    } else {
        categories
    }
}

enum class SearchSortOrder(val label: String) {
    LATEST("최신순"),
    HIGH_AMOUNT("높은 금액순"),
    LOW_AMOUNT("낮은 금액순")
}

sealed class SearchUiSideEffect : UiSideEffect {
    data object OnBack : SearchUiSideEffect()
}
