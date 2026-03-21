package com.jie.wealthmate.feature.budget.budgetYearDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.BudgetYearsHeader
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.ExpenseDonutChart
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.ExpenseFixed
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.Insight
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.MonthCompare
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.SavingGoal
import com.jie.wealthmate.theme.ColorGray
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BudgetYearDetailScreen(
    navController: NavController,
    selectedYear: String,
    viewModel: BudgetYearDetailViewModel = koinViewModel { parametersOf(selectedYear) },
) {
    BaseScreen(viewModel = viewModel) { uiState ->
        BudgetYearDetailContent(
            uiState = uiState,
            onBack = { navController.popBackStack() }
        )
    }
}

@Composable
fun BudgetYearDetailContent(
    uiState: BudgetYearDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(ColorGray.White)
    ) {
        WMTopBar(
            title = TopBarItem.Title("${uiState.selectedYear}년 예산 상세"),
            readingItem = TopBarItem.ReadingItem(action = onBack)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(ColorGray.Gray_50)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                BudgetYearsHeader(
                    actualIncome = uiState.totalActualIncome,
                    lastActualIncome = uiState.lastActualIncome,
                    actualExpense = uiState.totalActualExpense,
                    lastActualExpense = uiState.lastActualExpense,
                    actualSaving = uiState.totalActualSaving,
                    lastActualSaving = uiState.lastActualSaving,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 4.dp, bottom = 24.dp)
                )
            }

            item {
                MonthCompare(
                    year = uiState.selectedYear,
                    monthlyData = uiState.monthlyData,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(bottom = 24.dp)
                )
            }

            item {
                ExpenseDonutChart(
                    categories = uiState.topExpenseCategories,
                    totalExpense = uiState.totalVariableExpense,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(bottom = 24.dp)
                )
            }

            item {
                ExpenseFixed(
                    fixedExpenses = uiState.fixedExpenses,
                    totalExpense = uiState.totalActualExpense,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(bottom = 24.dp)
                )
            }
            item {
                SavingGoal(
                    budgetSaving = uiState.totalBudgetSaving,
                    actualSaving = uiState.totalActualSaving,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(bottom = 24.dp)
                )
            }

            item {
                Insight(
                    totalIncome = uiState.totalActualIncome,
                    totalExpense = uiState.totalActualExpense,
                    totalSaving = uiState.totalActualSaving,
                    monthlyData = uiState.monthlyData,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}
