package com.jie.wealthmate.feature.budget.budgetSetting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.addBudget.AddBudgetScreen
import com.jie.wealthmate.feature.budget.budgetDetail.BudgetDetailScreen
import com.jie.wealthmate.feature.budget.budgetSetting.component.BudgetMoreMenu
import com.jie.wealthmate.feature.budget.budgetSetting.component.MonthBudgetList
import com.jie.wealthmate.feature.budget.budgetSetting.component.MonthBudgetMoreModalBottomSheet
import com.jie.wealthmate.feature.budget.budgetSetting.component.YearChips
import kotlinx.datetime.LocalDate
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add


class BudgetSettingScreen : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: BudgetSettingScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowMoreBottomSheet by remember { mutableStateOf(false) }
        var targetYearMonth by remember { mutableStateOf("") }
        var hasBudgetByTarget by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            WMTopBar(
                title = TopBarItem.Title("예산 관리"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
                ),
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_add,
                        action = { navigator.push(AddBudgetScreen()) }
                    )
                )
            )

            YearChips(
                modifier = Modifier.padding(vertical = 8.dp),
                yearList = uiState.yearList,
                selectedYear = uiState.selectedYear,
                onYearSelected = screenModel::onYearSelected
            )

            Spacer(modifier = Modifier.height(12.dp))

            MonthBudgetList(
                modifier = Modifier.weight(1f),
                year = uiState.selectedYear,
                monthBudgetList = uiState.monthBudgets,
                yearlySummary = uiState.yearlySummary,
                onMoreClick = { yearMonth, hasBudget ->
                    targetYearMonth = yearMonth
                    hasBudgetByTarget = hasBudget
                    isShowMoreBottomSheet = true
                },
                onMonthClick = { yearMonth ->
                    val parts = yearMonth.split("-")
                    val date = LocalDate(parts[0].toInt(), parts[1].toInt(), 1)
                    navigator.push(BudgetDetailScreen(date))
                },
            )
        }

        if (isShowMoreBottomSheet) {
            MonthBudgetMoreModalBottomSheet(
                targetYearMonth = targetYearMonth,
                hasBudgetByTarget = hasBudgetByTarget,
                onItemSelected = { menu ->
                    when (menu) {
                        BudgetMoreMenu.ADD -> {
                            navigator.push(
                                AddBudgetScreen(
                                    selectedYearMonth = targetYearMonth,
                                    isEditMode = false
                                )
                            )
                        }

                        BudgetMoreMenu.MODIFY -> {
                            navigator.push(
                                AddBudgetScreen(
                                    selectedYearMonth = targetYearMonth,
                                    isEditMode = true
                                )
                            )
                        }

                        BudgetMoreMenu.COPY -> {
                            screenModel.showSnackbar("$targetYearMonth 예산이 복사되었습니다.")
                            navigator.push(
                                AddBudgetScreen(
                                    selectedYearMonth = targetYearMonth,
                                    isEditMode = true,
                                    isCopyMode = true
                                )
                            )
                        }

                        BudgetMoreMenu.DELETE -> {
                            showRemoveDialog {
                                screenModel.deleteBudget(targetYearMonth)
                            }
                        }
                    }
                    isShowMoreBottomSheet = false
                },
                onDismissRequest = { isShowMoreBottomSheet = false }
            )
        }
    }
}
