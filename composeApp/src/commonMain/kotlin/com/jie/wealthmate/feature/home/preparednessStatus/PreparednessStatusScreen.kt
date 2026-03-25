package com.jie.wealthmate.feature.home.preparednessStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.preparednessStatus.component.PreparednessStatusFilter
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusCategoryComparisonSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusDonutChartSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusFixedCategoryComparisonSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusPaymentMethodSection
import com.jie.wealthmate.feature.home.preparednessStatus.component.StatusSummaryCards
import com.jie.wealthmate.feature.home.preparednessStatus.component.SummarySection
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.CategoryDiffInfoVo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PreparednessStatusScreen(
    navController: NavController,
    initialStatusType: StatusType,
    initialLargeCategory: LargeCategoryEnum,
    scrollToPosition: Int = 0,
    viewModel: PreparednessStatusViewModel = koinViewModel {
        parametersOf(initialStatusType, initialLargeCategory)
    },
) {
    BaseScreen(
        viewModel = viewModel,
        onBack = { navController.popBackStack() }
    ) { uiState ->
        PreparednessStatusContent(
            uiState = uiState,
            scrollToPosition = scrollToPosition,
            onBackClick = { navController.popBackStack() },
            onUpdateStatusType = viewModel::updateStatusType,
            onUpdateLargeCategory = viewModel::updateLargeCategory,
            onCategoryClick = { categoryId ->
                navController.navigate("categoryExpenses/${uiState.statusType.name}/${uiState.largeCategory.name}/$categoryId/")
            },
            onPaymentMethodClick = { paymentMethodId ->
                navController.navigate("paymentMethodExpenses/${uiState.statusType.name}/${uiState.largeCategory.name}/$paymentMethodId")
            }
        )
    }
}

@Composable
fun PreparednessStatusContent(
    uiState: PreparednessStatusUiState,
    scrollToPosition: Int,
    onBackClick: () -> Unit,
    onUpdateStatusType: (StatusType) -> Unit,
    onUpdateLargeCategory: (LargeCategoryEnum) -> Unit,
    onCategoryClick: (String?) -> Unit,
    onPaymentMethodClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowStatusTypeModal by remember { mutableStateOf(false) }
    var isShowLargeCategoryModal by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(scrollToPosition, uiState.categoryComparisons, uiState.paymentMethodComparisons) {
        if (scrollToPosition > 0) {
            // scrollToPosition 5는 결제수단 섹션인데, 데이터가 아직 없을 때 스크롤하면 무시될 수 있음
            // 데이터가 로드된 후(비어있지 않을 때) 스크롤 수행
            when {
                scrollToPosition >= 4 && uiState.paymentMethodComparisons.isNotEmpty() -> {
                    listState.animateScrollToItem(scrollToPosition)
                }
                scrollToPosition >= 2 && uiState.categoryComparisons.isNotEmpty() -> {
                    listState.animateScrollToItem(scrollToPosition)
                }
                scrollToPosition < 2 -> {
                    listState.animateScrollToItem(scrollToPosition)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(ColorGray.White)
    ) {
        WMTopBar(
            title = TopBarItem.Title("현황"),
            readingItem = TopBarItem.ReadingItem(
                action = onBackClick
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
                                title = "카테고리별 ${uiState.largeCategory.label} 비중",
                                totalLabel = uiState.largeCategory.label,
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
                        title = "변동${uiState.largeCategory.label} 카테고리별 비교",
                        statusType = uiState.statusType,
                        largeCategory = uiState.largeCategory,
                        comparisons = uiState.categoryComparisons,
                        onCategoryClick = onCategoryClick
                    )
                }

                if (uiState.fixedCategoryComparisons.isNotEmpty()) {
                    StatusFixedCategoryComparisonSection(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .padding(horizontal = 28.dp),
                        title = "고정${uiState.largeCategory.label} 카테고리별 비교",
                        statusType = uiState.statusType,
                        largeCategory = uiState.largeCategory,
                        comparisons = uiState.fixedCategoryComparisons,
                        onCategoryClick = onCategoryClick
                    )
                }
            }

            if (uiState.largeCategory == LargeCategoryEnum.EXPENSES && uiState.paymentMethodComparisons.isNotEmpty()) {
                item {
                    StatusPaymentMethodSection(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .padding(horizontal = 28.dp),
                        statusType = uiState.statusType,
                        largeCategory = LargeCategoryEnum.EXPENSES,
                        comparisons = uiState.paymentMethodComparisons,
                        onPaymentMethodClick = onPaymentMethodClick
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
            onItemSelected = {
                onUpdateStatusType(it)
                isShowStatusTypeModal = false
            },
            onDismissRequest = { isShowStatusTypeModal = false }
        )
    }

    if (isShowLargeCategoryModal) {
        WMListSelectionModalBottomSheet(
            title = "구분 선택",
            items = LargeCategoryEnum.entries,
            selectedItem = uiState.largeCategory,
            itemLabel = { it.label },
            onItemSelected = {
                onUpdateLargeCategory(it)
                isShowLargeCategoryModal = false
            },
            onDismissRequest = { isShowLargeCategoryModal = false }
        )
    }
}

@Preview
@Composable
private fun PreparednessStatusContentPreview() {
    WMTheme {
        PreparednessStatusContent(
            uiState = PreparednessStatusUiState(
                statusType = StatusType.MONTH,
                largeCategory = LargeCategoryEnum.EXPENSES,
                currentAmount = 450000,
                lastAmount = 400000,
                maxIncreaseCategory = CategoryDiffInfoVo(
                    categoryId = "1",
                    categoryIcon = "🍔",
                    categoryName = "식비",
                    currentAmount = 150000,
                    diffAmount = 30000,
                    ratio = 0.33f
                ),
                categoryComparisons = listOf(
                    CategoryDiffInfoVo(
                        categoryId = "1",
                        categoryIcon = "🍔",
                        categoryName = "식비",
                        currentAmount = 150000,
                        diffAmount = 30000,
                        ratio = 0.33f
                    )
                )
            ),
            scrollToPosition = 0,
            onBackClick = {},
            onUpdateStatusType = {},
            onUpdateLargeCategory = {},
            onCategoryClick = {},
            onPaymentMethodClick = {}
        )
    }
}
