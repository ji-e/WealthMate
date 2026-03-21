package com.jie.wealthmate.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMHorizontalDivider
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.feature.home.component.CategorySegmentedChart
import com.jie.wealthmate.feature.home.component.ExpensesLineChart
import com.jie.wealthmate.feature.home.component.LargeCategoryStatus
import com.jie.wealthmate.feature.home.component.PaymentMethodSegmentedChart
import com.jie.wealthmate.feature.home.component.RecurringHistory
import com.jie.wealthmate.feature.home.component.RemainBudget
import com.jie.wealthmate.feature.home.component.Today
import com.jie.wealthmate.feature.home.component.vo.AmountVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.default
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel(),
) {
    BaseScreen(
        viewModel = viewModel,
    ) { uiState ->
        HomeContent(
            uiState = uiState,
            onUpdateStatusType = viewModel::updateStatusType,
            onLargeCategoryClick = { largeCategory ->
                navController.navigate("preparednessStatus/${uiState.statusType.name}/${largeCategory.name}")
            },
            onCategoryChartClick = {
                navController.navigate("preparednessStatus/${uiState.statusType.name}/${LargeCategoryEnum.EXPENSES.name}?scrollToPosition=2")
            },
            onCategoryItemClick = { categoryId ->
                navController.navigate("categoryExpenses/${uiState.statusType.name}/${LargeCategoryEnum.EXPENSES.name}/$categoryId")
            },
            onPaymentMethodChartClick = {
                navController.navigate("preparednessStatus/${uiState.statusType.name}/${LargeCategoryEnum.EXPENSES.name}?scrollToPosition=5")
            },
            onPaymentMethodItemClick = { paymentMethodId ->
                navController.navigate("paymentMethodExpenses/${uiState.statusType.name}/${LargeCategoryEnum.EXPENSES.name}/$paymentMethodId")
            },
            onRecurringHeaderClick = {
                navController.navigate("repeatHistoryManagement/${LargeCategoryEnum.EXPENSES.name}")
            },
            onRecurringItemClick = { id ->
                navController.navigate("repeatHistoryDetail/$id")
            }
        )
    }
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onUpdateStatusType: (StatusType) -> Unit,
    onLargeCategoryClick: (LargeCategoryEnum) -> Unit,
    onCategoryChartClick: () -> Unit,
    onCategoryItemClick: (String?) -> Unit,
    onPaymentMethodChartClick: () -> Unit,
    onPaymentMethodItemClick: (String?) -> Unit,
    onRecurringHeaderClick: () -> Unit,
    onRecurringItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowStatusTypeModalBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 44.dp, bottom = calculateAdjustedToastPadding(124))
            .verticalScroll(rememberScrollState()),
    ) {

        WMSpacer()
        Today(
            modifier = Modifier.padding(horizontal = Padding.BackgroundHorizontal),
            todayAmount = uiState.todayAmount,
            budgetAmount = uiState.thisMonthBudgetAmount,
            expensesAmount = uiState.thisMonthExpensesAmount
        )

        if (uiState.thisMonthBudgetAmount > 0L) {
            WMSpacer(size = SpacerSize.LARGE)
            RemainBudget(
                modifier = Modifier.padding(horizontal = Padding.BackgroundHorizontal),
                budgetAmount = uiState.thisMonthBudgetAmount,
                expensesAmount = uiState.thisMonthExpensesAmount
            )
        }

        WMHorizontalDivider(
            modifier = Modifier.padding(top = Padding.SpacerM),
            thickness = 12.dp
        )

        WMSpacer()
        Row(
            modifier = Modifier
                .padding(horizontal = Padding.BackgroundHorizontal)
                .noRippleClickable(
                    onClick = { isShowStatusTypeModalBottomSheet = true }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = uiState.statusType.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_drop_down),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        WMSpacer(size = SpacerSize.SMALL)
        LargeCategoryStatus(
            statusType = uiState.statusType,
            currentAmount = uiState.currentAmount,
            lastAmount = uiState.lastAmount,
            onCategoryClick = onLargeCategoryClick
        )

        WMSpacer(size = SpacerSize.LARGE)
        ExpensesLineChart(
            modifier = Modifier.padding(horizontal = Padding.BackgroundHorizontal),
            statusType = uiState.statusType,
            currentData = uiState.currentExpensesData,
            lastData = uiState.lastExpensesData
        )

        WMSpacer(size = SpacerSize.LARGE)
        CategorySegmentedChart(
            statusType = uiState.statusType,
            expensesAmount = uiState.currentAmount?.expensesAmount.default(),
            categorySegmentChartItems = uiState.categorySegmentChartItems,
            onCategoryChartClick = onCategoryChartClick,
            onCategoryItemClick = onCategoryItemClick
        )

        WMSpacer(size = SpacerSize.LARGE)
        PaymentMethodSegmentedChart(
            statusType = uiState.statusType,
            expensesAmount = uiState.currentAmount?.expensesAmount.default(),
            paymentMethodSegmentChartItems = uiState.paymentMethodSegmentChartItems,
            onPaymentMethodChartClick = onPaymentMethodChartClick,
            onPaymentMethodItemClick = onPaymentMethodItemClick
        )

        WMHorizontalDivider(
            modifier = Modifier.padding(vertical = Padding.SpacerL),
            thickness = 12.dp
        )

        RecurringHistory(
            recurringHistories = uiState.sortedRecurringHistories,
            totalAmount = uiState.totalRecurringAmount,
            passedAmount = uiState.passedRecurringAmount,
            onHeaderClick = onRecurringHeaderClick,
            onItemClick = onRecurringItemClick
        )

        Spacer(modifier = Modifier.height(Padding.SpacerL))
    }

    if (isShowStatusTypeModalBottomSheet) {
        WMListSelectionModalBottomSheet(
            title = "기간 선택",
            items = StatusType.entries,
            selectedItem = uiState.statusType,
            itemLabel = { it.label },
            onItemSelected = {
                onUpdateStatusType(it)
                isShowStatusTypeModalBottomSheet = false
            },
            onDismissRequest = { isShowStatusTypeModalBottomSheet = false }
        )
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    WMTheme {
        HomeContent(
            uiState = HomeUiState(
                todayAmount = 15400,
                thisMonthBudgetAmount = 1000000,
                thisMonthExpensesAmount = 450000,
                currentAmount = AmountVo(
                    expensesAmount = 450000,
                    incomeAmount = 2500000,
                    savingAmount = 500000
                ),
                lastAmount = AmountVo(
                    expensesAmount = 500000,
                    incomeAmount = 2500000,
                    savingAmount = 500000
                ),
                statusType = StatusType.MONTH
            ),
            onUpdateStatusType = {},
            onLargeCategoryClick = {},
            onCategoryChartClick = {},
            onCategoryItemClick = {},
            onPaymentMethodChartClick = {},
            onPaymentMethodItemClick = {},
            onRecurringHeaderClick = {},
            onRecurringItemClick = {}
        )
    }
}
