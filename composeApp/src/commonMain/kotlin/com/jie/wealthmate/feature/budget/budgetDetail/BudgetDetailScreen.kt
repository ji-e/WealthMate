package com.jie.wealthmate.feature.budget.budgetDetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.budgetDetail.component.BudgetMonthlyHeader
import com.jie.wealthmate.feature.budget.budgetDetail.component.CategoryBudgetGroup
import com.jie.wealthmate.feature.budget.budgetDetail.component.SectionHeader
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BudgetDetailScreen(
    navController: NavController,
    selectedMonth: LocalDate,
    viewModel: BudgetDetailViewModel = koinViewModel { parametersOf(selectedMonth) },
) {
    BaseScreen(viewModel = viewModel) { uiState ->
        BudgetDetailContent(
            uiState = uiState,
            onBack = { navController.popBackStack() }
        )
    }
}

@Composable
fun BudgetDetailContent(
    uiState: BudgetDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expandedStates = remember { mutableStateMapOf<LargeCategoryEnum, Boolean>() }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
            .background(ColorGray.White)
    ) {
        WMTopBar(
            title = TopBarItem.Title("${uiState.selectedMonth.year}년 ${uiState.selectedMonth.monthNumber}월 예산 상세"),
            readingItem = TopBarItem.ReadingItem(action = onBack)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorGray.Gray_50)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                BudgetMonthlyHeader(
                    actualIncome = uiState.totalIncome,
                    lastActualIncome = uiState.lastTotalIncome,
                    actualExpense = uiState.totalExpense,
                    lastActualExpense = uiState.lastTotalExpense,
                    actualSaving = uiState.totalSaving,
                    lastActualSaving = uiState.lastTotalSaving,
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 4.dp, bottom = 24.dp)
                )
            }

            uiState.sections.forEach { section ->
                if (section.groups.isNotEmpty()) {
                    val isExpanded = expandedStates[section.largeCategory] ?: true

                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 28.dp)
                                .background(ColorGray.White, RoundedCornerShape(8.dp))
                                .padding(vertical = 4.dp)
                        ) {
                            SectionHeader(
                                section = section,
                                isExpanded = isExpanded,
                                onToggle = {
                                    expandedStates[section.largeCategory] = !isExpanded
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                                        color = ColorGray.Gray_100
                                    )
                                    section.groups.forEach { group ->
                                        CategoryBudgetGroup(
                                            group = group,
                                            largeCategory = section.largeCategory,
                                            modifier = Modifier
                                                .padding(start = 10.dp, end = 16.dp)
                                                .padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
