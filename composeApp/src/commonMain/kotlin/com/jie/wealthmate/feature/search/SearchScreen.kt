package com.jie.wealthmate.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.WMMultiListSelectionModalBottomSheet
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.component.textField.WMSearchTextField
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.search.component.DateRangeSelectModalBottomSheet
import com.jie.wealthmate.feature.search.component.SearchFilterRow

class SearchScreen : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SearchScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var showSortBottomSheet by remember { mutableStateOf(false) }
        var showDateRangeBottomSheet by remember { mutableStateOf(false) }
        var showTypeBottomSheet by remember { mutableStateOf(false) }
        var showCategoryBottomSheet by remember { mutableStateOf(false) }
        var showPaymentMethodBottomSheet by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
            WMTopBar(
                title = TopBarItem.Title("내역 검색"),
                readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
            )

            WMSearchTextField(
                modifier = Modifier
                    .padding(start = 28.dp, end = 16.dp)
                    .padding(top = 4.dp, bottom = 20.dp),
                value = uiState.query,
                onValueChange = screenModel::updateQuery,
                onSearch = { screenModel.search() },
                onClear = { screenModel.clearQuery() },
                placeholder = "검색할 카테고리, 내용을 입력해 주세요."
            )

            SearchFilterRow(
                sortOrder = uiState.sortOrder,
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                selectedLargeCategories = uiState.selectedLargeCategories,
                selectedCategories = uiState.selectedCategories,
                selectedPaymentMethods = uiState.selectedPaymentMethods,
                totalPaymentMethods = uiState.paymentMethods.size,
                totalCategories = uiState.categories.size,
                onSortClick = { showSortBottomSheet = true },
                onPeriodClick = { showDateRangeBottomSheet = true },
                onCategoryClick = { showCategoryBottomSheet = true },
                onLargeCategoryClick = { showTypeBottomSheet = true },
                onPaymentMethodClick = { showPaymentMethodBottomSheet = true },
                onResetClick = screenModel::resetFilters
            )

            // TODO: Implement search results list
        }

        if (showSortBottomSheet) {
            WMListSelectionModalBottomSheet(
                title = "정렬",
                items = SearchSortOrder.entries,
                selectedItem = uiState.sortOrder,
                itemLabel = { it.label },
                onItemSelected = screenModel::updateSortOrder,
                onDismissRequest = { showSortBottomSheet = false }
            )
        }

        if (showDateRangeBottomSheet) {
            DateRangeSelectModalBottomSheet(
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                onSelectClick = screenModel::updateDateRange,
                onDismissRequest = { showDateRangeBottomSheet = false }
            )
        }

        if (showTypeBottomSheet) {
            WMMultiListSelectionModalBottomSheet(
                title = "거래구분",
                items = LargeCategoryEnum.entries,
                selectedItems = uiState.selectedLargeCategories,
                itemLabel = { it.label },
                onItemsSelected = screenModel::updateLargeCategories,
                onDismissRequest = { showTypeBottomSheet = false }
            )
        }

        if (showCategoryBottomSheet) {
            val filteredCategories = if (uiState.selectedLargeCategories.isNotEmpty()) {
                uiState.categories.filter { uiState.selectedLargeCategories.contains(it.largeCategory) }
            } else {
                uiState.categories
            }

            WMMultiListSelectionModalBottomSheet(
                title = "카테고리",
                items = filteredCategories,
                selectedItems = uiState.selectedCategories,
                itemLabel = { "${it.largeCategory.label} > ${it.middleLabel} ${if (it.isFixed) "| 고정" else ""}" },
                onItemsSelected = screenModel::updateCategories,
                onDismissRequest = { showCategoryBottomSheet = false }
            )
        }

        if (showPaymentMethodBottomSheet) {
            WMMultiListSelectionModalBottomSheet(
                title = "결제수단",
                items = uiState.paymentMethods,
                selectedItems = uiState.selectedPaymentMethods,
                itemLabel = { it.label },
                onItemsSelected = screenModel::updatePaymentMethods,
                onDismissRequest = { showPaymentMethodBottomSheet = false }
            )
        }
    }
}
