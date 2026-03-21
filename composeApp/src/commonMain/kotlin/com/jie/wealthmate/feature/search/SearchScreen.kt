package com.jie.wealthmate.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.WMMultiListSelectionModalBottomSheet
import com.jie.wealthmate.component.textField.WMSearchTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.search.component.CategoryMultiSelectModalBottomSheet
import com.jie.wealthmate.feature.search.component.DateRangeSelectModalBottomSheet
import com.jie.wealthmate.feature.search.component.SearchFilterRow
import com.jie.wealthmate.feature.search.component.SearchResult
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = koinViewModel(),
) {
    BaseScreen(viewModel = viewModel) { uiState ->
        SearchContent(
            uiState = uiState,
            onBack = { navController.popBackStack() },
            onUpdateQuery = viewModel::updateQuery,
            onSearch = { viewModel.search(isFirstPage = true) },
            onClearQuery = viewModel::clearQuery,
            onUpdateSortOrder = viewModel::updateSortOrder,
            onUpdateDateRange = viewModel::updateDateRange,
            onUpdateLargeCategories = viewModel::updateLargeCategories,
            onUpdateCategories = viewModel::updateCategories,
            onUpdatePaymentMethods = viewModel::updatePaymentMethods,
            onResetFilters = viewModel::resetFilters,
            onLoadMore = viewModel::loadMore,
            onHistoryClick = { history ->
                navController.navigate("historyDetail/${history.largeCategory.name}/${history.id}")
            }
        )
    }
}

@Composable
fun SearchContent(
    uiState: SearchUiState,
    onBack: () -> Unit,
    onUpdateQuery: (androidx.compose.ui.text.input.TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    onClearQuery: () -> Unit,
    onUpdateSortOrder: (SearchSortOrder) -> Unit,
    onUpdateDateRange: (kotlinx.datetime.LocalDate?, kotlinx.datetime.LocalDate?) -> Unit,
    onUpdateLargeCategories: (List<LargeCategoryEnum>) -> Unit,
    onUpdateCategories: (List<com.jie.wealthmate.vo.CategoryVo>) -> Unit,
    onUpdatePaymentMethods: (List<com.jie.wealthmate.vo.PaymentMethodVo>) -> Unit,
    onResetFilters: () -> Unit,
    onLoadMore: () -> Unit,
    onHistoryClick: (com.jie.wealthmate.vo.HistoryVo) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSortBottomSheet by remember { mutableStateOf(false) }
    var showDateRangeBottomSheet by remember { mutableStateOf(false) }
    var showTypeBottomSheet by remember { mutableStateOf(false) }
    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    var showPaymentMethodBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .imePadding()
    ) {
        WMTopBar(
            title = TopBarItem.Title("내역 검색"),
            readingItem = TopBarItem.ReadingItem().copy(action = onBack),
        )

        WMSearchTextField(
            modifier = Modifier
                .padding(start = 28.dp, end = 16.dp)
                .padding(top = 4.dp, bottom = 20.dp),
            value = uiState.query,
            onValueChange = onUpdateQuery,
            onSearch = onSearch,
            onClear = onClearQuery,
            placeholder = "검색할 카테고리, 내용을 입력해 주세요."
        )

        SearchFilterRow(
            sortOrder = uiState.sortOrder,
            startDate = uiState.startDate,
            endDate = uiState.endDate,
            selectedLargeCategories = uiState.selectedLargeCategories,
            selectedCategories = uiState.selectedCategories,
            selectedPaymentMethods = uiState.selectedPaymentMethods,
            onSortClick = { showSortBottomSheet = true },
            onPeriodClick = { showDateRangeBottomSheet = true },
            onCategoryClick = { showCategoryBottomSheet = true },
            onLargeCategoryClick = { showTypeBottomSheet = true },
            onPaymentMethodClick = { showPaymentMethodBottomSheet = true },
            onResetClick = onResetFilters
        )

        SearchResult(
            startDate = uiState.startDate,
            endDate = uiState.endDate,
            summary = uiState.summary,
            selectedLargeCategories = uiState.selectedLargeCategories,
            searchResults = uiState.searchResults,
            sortOrder = uiState.sortOrder,
            isLoading = uiState.isLoading,
            hasMore = uiState.hasMore,
            onLoadMore = onLoadMore,
            onHistoryClick = onHistoryClick
        )
    }

    if (showSortBottomSheet) {
        WMListSelectionModalBottomSheet(
            title = "정렬",
            items = SearchSortOrder.entries,
            selectedItem = uiState.sortOrder,
            itemLabel = { it.label },
            onItemSelected = {
                onUpdateSortOrder(it)
                showSortBottomSheet = false
            },
            onDismissRequest = { showSortBottomSheet = false }
        )
    }

    if (showDateRangeBottomSheet) {
        DateRangeSelectModalBottomSheet(
            startDate = uiState.startDate,
            endDate = uiState.endDate,
            onSelectClick = { start, end ->
                onUpdateDateRange(start, end)
                showDateRangeBottomSheet = false
            },
            onDismissRequest = { showDateRangeBottomSheet = false }
        )
    }

    if (showTypeBottomSheet) {
        WMMultiListSelectionModalBottomSheet(
            title = "거래구분",
            items = LargeCategoryEnum.entries,
            selectedItems = uiState.selectedLargeCategories,
            itemLabel = { it.label },
            onItemsSelected = {
                onUpdateLargeCategories(it)
                showTypeBottomSheet = false
            },
            onDismissRequest = { showTypeBottomSheet = false }
        )
    }

    if (showCategoryBottomSheet) {
        CategoryMultiSelectModalBottomSheet(
            title = "카테고리",
            categories = uiState.filteredCategories,
            selectedCategories = uiState.selectedCategories,
            onItemsSelected = {
                onUpdateCategories(it)
                showCategoryBottomSheet = false
            },
            onDismissRequest = { showCategoryBottomSheet = false },
            showHeaders = true,
            showUnset = true,
            largeCategoryFilter = uiState.selectedLargeCategories
        )
    }

    if (showPaymentMethodBottomSheet) {
        WMMultiListSelectionModalBottomSheet(
            title = "결제수단",
            items = uiState.paymentMethods,
            selectedItems = uiState.selectedPaymentMethods,
            itemLabel = {
                "${it.label}${
                    if (it.groupLabel.isNullOrBlank().not()) " | " + it.groupLabel else ""
                }"
            },
            onItemsSelected = {
                onUpdatePaymentMethods(it)
                showPaymentMethodBottomSheet = false
            },
            onDismissRequest = { showPaymentMethodBottomSheet = false }
        )
    }
}
