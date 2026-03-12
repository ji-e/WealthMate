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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.budgetDetail.component.BudgetMonthlyHeader
import com.jie.wealthmate.feature.budget.budgetDetail.component.CategoryBudgetGroup
import com.jie.wealthmate.feature.budget.budgetDetail.component.SectionHeader
import com.jie.wealthmate.theme.ColorGray
import kotlinx.datetime.LocalDate
import org.koin.core.parameter.parametersOf

class BudgetDetailScreen(
    private val selectedMonth: LocalDate,
) : BaseScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: BudgetDetailScreenModel = koinScreenModel { parametersOf(selectedMonth) }
        val uiState by screenModel.container.uiState.collectAsState()

        val expandedStates =
            remember { mutableStateMapOf<com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum, Boolean>() }

        Column(modifier = Modifier.fillMaxSize().background(ColorGray.White)) {
            WMTopBar(
                title = TopBarItem.Title("${uiState.selectedMonth.year}년 ${uiState.selectedMonth.monthNumber}월 예산 상세"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    BudgetMonthlyHeader(
                        income = uiState.totalIncome,
                        expense = uiState.totalExpense,
                        saving = uiState.totalSaving,
                        modifier = Modifier
                            .padding(horizontal = 28.dp)
                            .padding(top = 4.dp, bottom = 32.dp)
                    )
                }

                uiState.sections.forEach { section ->
                    if (section.groups.isNotEmpty()) {
                        val isExpanded = expandedStates[section.largeCategory] ?: true

                        item {

                            SectionHeader(
                                section = section,
                                isExpanded = isExpanded,
                                onToggle = {
                                    expandedStates[section.largeCategory] = !isExpanded
                                },
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)
                            )
                        }

                        item {
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column {
                                    section.groups.forEach { group ->
                                        CategoryBudgetGroup(
                                            group = group,
                                            largeCategory = section.largeCategory,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
