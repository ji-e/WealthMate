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
import com.jie.wealthmate.feature.budget.budgetSetting.BudgetSettingScreen
import com.jie.wealthmate.feature.budget.component.BudgetHeader
import com.jie.wealthmate.feature.budget.component.BudgetOverPager
import com.jie.wealthmate.feature.budget.component.BudgetSummary
import com.jie.wealthmate.feature.calendar.component.SelectedCalendarModalBottomSheet
import com.jie.wealthmate.feature.calendar.startDate
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_setting


class BudgetScreen() : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: BudgetScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }

        val monthItems = remember {
            val totalMonths = (today.year - startDate.year) * 12 + 12
            List(totalMonths) { startDate.plus(it, DateTimeUnit.MONTH) }
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
                readingItem = null,
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
                    modifier = Modifier.padding(horizontal = 28.dp),
                    selectedMonth = uiState.selectedMonth,
                    onSelectedMonthClick = { isShowSelectedCalendarModalBottomSheet = true },
                    usedAmount = uiState.usedAmount,
                    budgetAmount = uiState.totalBudgetAmount
                )

                // 해당 년월에 예산이 없을 때
                if (uiState.totalBudgetAmount == 0L) {
                    EmptyListView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentText = "설정된 예산이 없습니다."
                    )
                    return@Column
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    color = ColorGray.Gray_50,
                    thickness = 12.dp
                )

                BudgetOverPager(
                    items = uiState.budgetOverItems
                )

                BudgetSummary(
                    modifier = Modifier.padding(top = 32.dp),
                    items = uiState.budgetSummaryItems
                )

                Spacer(modifier = Modifier.height(32.dp))

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