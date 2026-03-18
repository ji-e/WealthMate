package com.jie.wealthmate.feature.home.preparednessStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.categoryExpenses.CategoryExpensesScreen
import com.jie.wealthmate.feature.home.preparednessStatus.component.PreparednessStatusFilter
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusCategoryComparisonSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusDonutChartSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusFixedCategoryComparisonSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusPaymentMethodSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusSummaryCards
import com.jie.wealthmate.feature.home.preparednessStatus.component.SummarySection
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import org.koin.core.parameter.parametersOf

class PreparednessStatusScreen(
    private val initialStatusType: StatusType,
    private val initialLargeCategory: LargeCategoryEnum,
    private val scrollToPosition: Int = 0,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: PreparednessStatusScreenModel = koinScreenModel {
            parametersOf(initialStatusType, initialLargeCategory)
        }
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowStatusTypeModal by remember { mutableStateOf(false) }
        var isShowLargeCategoryModal by remember { mutableStateOf(false) }

        val listState = rememberLazyListState()

        LaunchedEffect(scrollToPosition) {
            if (scrollToPosition > 0) {
                listState.animateScrollToItem(scrollToPosition)
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            WMTopBar(
                title = TopBarItem.Title("현황"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
                ),
                trailingCustomItem = TopBarItem.TrailingCustomItem {
                    PreparednessStatusFilter(
                        statusTypeLabel = uiState.statusType.label,
                        largeCategoryLabel = uiState.largeCategory.label,
                        onStatusTypeClick = { isShowStatusTypeModal = true },
                        onLargeCategoryClick = { isShowLargeCategoryModal = true }
                    )
                }
            )
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorGray.Gray_50)
            ) {

                item {
                    // 요약 섹션
                    SummarySection(
                        statusType = uiState.statusType,
                        largeCategory = uiState.largeCategory,
                        diffPercentage = uiState.diffPercentage,
                        diffAmount = uiState.diffAmount,
                        currentAmount = uiState.currentAmount,
                    )
                }
                item {
                    when (uiState.largeCategory) {
                        LargeCategoryEnum.INCOME, LargeCategoryEnum.SAVING -> {
                            if (uiState.currentAmount > 0) {
                                StatusDonutChartSection(
                                    modifier = Modifier
                                        .padding(top = 24.dp)
                                        .padding(horizontal = 28.dp),
                                    title = "카테고리별 수입 비중",
                                    totalLabel = "수입",
                                    categories = uiState.categoryComparisons + uiState.fixedCategoryComparisons
                                )
                            }
                        }

                        LargeCategoryEnum.EXPENSES -> {
                            StatusSummaryCards(
                                modifier = Modifier
                                    .padding(top = 24.dp)
                                    .padding(horizontal = 28.dp),
                                maxIncreaseCategory = uiState.maxIncreaseCategory,
                                maxDecreaseCategory = uiState.maxDecreaseCategory,
                                largeCategory = LargeCategoryEnum.EXPENSES
                            )
                        }
                    }
                }

                item {
                    if (uiState.categoryComparisons.isNotEmpty()) {
                        StatusCategoryComparisonSection(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .padding(horizontal = 28.dp),
                            title = "변동지출 카테고리별 비교",
                            statusType = uiState.statusType,
                            largeCategory = uiState.largeCategory,
                            comparisons = uiState.categoryComparisons,
                            onCategoryClick = { categoryId ->
                                navigator.push(
                                    CategoryExpensesScreen(
                                        uiState.statusType,
                                        uiState.largeCategory,
                                        categoryId
                                    )
                                )
                            }
                        )
                    }

                    if (uiState.fixedCategoryComparisons.isNotEmpty()) {
                        StatusFixedCategoryComparisonSection(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .padding(horizontal = 28.dp),
                            title = "고정지출 카테고리별 비교",
                            statusType = uiState.statusType,
                            largeCategory = uiState.largeCategory,
                            comparisons = uiState.fixedCategoryComparisons,
                            onCategoryClick = { categoryId ->
                                navigator.push(
                                    CategoryExpensesScreen(
                                        uiState.statusType,
                                        uiState.largeCategory,
                                        categoryId
                                    )
                                )
                            }
                        )
                    }
                }

                if (uiState.largeCategory == LargeCategoryEnum.EXPENSES
                    && uiState.paymentMethodComparisons.isNotEmpty()
                ) {
                    item {
                        StatusPaymentMethodSection(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .padding(horizontal = 28.dp),
                            statusType = uiState.statusType,
                            largeCategory = LargeCategoryEnum.EXPENSES,
                            comparisons = uiState.paymentMethodComparisons
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        if (isShowStatusTypeModal) {
            WMListSelectionModalBottomSheet(
                title = "기간 선택",
                items = StatusType.entries,
                selectedItem = uiState.statusType,
                itemLabel = { it.label },
                onItemSelected = { screenModel.updateStatusType(it) },
                onDismissRequest = { isShowStatusTypeModal = false }
            )
        }

        if (isShowLargeCategoryModal) {
            WMListSelectionModalBottomSheet(
                title = "카테고리 선택",
                items = LargeCategoryEnum.entries,
                selectedItem = uiState.largeCategory,
                itemLabel = { it.label },
                onItemSelected = { screenModel.updateLargeCategory(it) },
                onDismissRequest = { isShowLargeCategoryModal = false }
            )
        }
    }
}
