package com.jie.wealthmate.feature.budget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.budgetDetail.BudgetDetailScreen
import com.jie.wealthmate.feature.budget.budgetSetting.BudgetSettingScreen
import com.jie.wealthmate.feature.budget.component.BudgetHeader
import com.jie.wealthmate.feature.budget.component.BudgetInfo
import com.jie.wealthmate.feature.budget.component.BudgetOverPager
import com.jie.wealthmate.feature.budget.component.BudgetSuccess
import com.jie.wealthmate.feature.budget.component.BudgetSummary
import com.jie.wealthmate.feature.calendar.component.SelectedCalendarModalBottomSheet
import com.jie.wealthmate.feature.calendar.START_DATE
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_setting

class BudgetScreen : BaseScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: BudgetScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }

        // 시작일이 바뀔 때만 재계산
        val monthItems = remember(START_DATE) {
            val totalMonths = (today.year - START_DATE.year) * 12 + 12
            List(totalMonths) { START_DATE.plus(it, DateTimeUnit.MONTH) }
        }

        val onMonthChanged = remember(screenModel) {
            { month: LocalDate -> screenModel.updateSelectedMonth(month) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateAdjustedToastPadding(80)),
        ) {
            WMTopBar(
                title = TopBarItem.Title("예산"),
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_setting,
                        action = { navigator.push(BudgetSettingScreen()) }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                BudgetHeader(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    selectedMonth = uiState.selectedMonth,
                    onSelectedMonthClick = { isShowSelectedCalendarModalBottomSheet = true },
                    usedAmount = uiState.usedAmount,
                    budgetAmount = uiState.totalBudgetAmount
                )

                // 전체 카테고리 중 설정된 예산이 하나도 없는 경우 확인
                val hasAnyBudget = uiState.budgetSummaryItems.any { it.budgetAmount > 0L }

                if (hasAnyBudget.not()) {
                    EmptyListView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentText = "설정된 예산이 없습니다."
                    )
                } else {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = ColorGray.Gray_50,
                        thickness = 12.dp
                    )

                    // 상태별 예산 정보 섹션
                    when {
                        uiState.budgetOverItems.isNotEmpty() -> {
                            BudgetOverPager(items = uiState.budgetOverItems)
                        }
                        uiState.totalBudgetAmount > 0L && (uiState.totalBudgetAmount - uiState.usedAmount) > 0 -> {
                            BudgetSuccess()
                        }
                        else -> {
                            BudgetInfo(
                                isNotBudgetSetting = uiState.totalBudgetAmount == 0L,
                                topExpenses = uiState.topExpenses
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    BudgetSummary(
                        items = uiState.budgetSummaryItems,
                        onDetailClick = {
                            navigator.push(BudgetDetailScreen(uiState.selectedMonth))
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (isShowSelectedCalendarModalBottomSheet) {
            SelectedCalendarModalBottomSheet(
                monthItem = monthItems,
                selectedMonth = uiState.selectedMonth,
                onMonthChange = { month ->
                    onMonthChanged(month)
                    isShowSelectedCalendarModalBottomSheet = false
                },
                onDismissRequest = { isShowSelectedCalendarModalBottomSheet = false }
            )
        }
    }
}
