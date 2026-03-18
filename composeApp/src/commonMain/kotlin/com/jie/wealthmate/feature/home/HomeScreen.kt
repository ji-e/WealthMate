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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.feature.home.categoryExpenses.CategoryExpensesScreen
import com.jie.wealthmate.feature.home.component.CategorySegmentedChart
import com.jie.wealthmate.feature.home.component.ExpensesLineChart
import com.jie.wealthmate.feature.home.component.LargeCategoryStatus
import com.jie.wealthmate.feature.home.component.PaymentMethodSegmentedChart
import com.jie.wealthmate.feature.home.component.RemainBudget
import com.jie.wealthmate.feature.home.component.Today
import com.jie.wealthmate.feature.home.preparednessStatus.PreparednessStatusScreen
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.default
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down

class HomeScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: HomeScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowStatusTypeModalBottomSheet by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp, bottom = calculateAdjustedToastPadding(80))
                .verticalScroll(rememberScrollState()),
        ) {

            Spacer(modifier = Modifier.height(28.dp))

            Today(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 24.dp),
                todayAmount = uiState.todayAmount,
                budgetAmount = uiState.thisMonthBudgetAmount,
                expensesAmount = uiState.thisMonthExpensesAmount
            )

            if (uiState.thisMonthBudgetAmount > 0L) {
                RemainBudget(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(top = 36.dp),
                    budgetAmount = uiState.thisMonthBudgetAmount,
                    expensesAmount = uiState.thisMonthExpensesAmount
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 24.dp),
                color = ColorGray.Gray_50,
                thickness = 12.dp
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 24.dp)
                    .noRippleClickable(
                        onClick = { isShowStatusTypeModalBottomSheet = true }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMText(
                    text = uiState.statusType.label,
                    style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            LargeCategoryStatus(
                modifier = Modifier.padding(top = 16.dp),
                statusType = uiState.statusType,
                currentAmount = uiState.currentAmount,
                lastAmount = uiState.lastAmount,
                onCategoryClick = { largeCategory ->
                    navigator.push(
                        PreparednessStatusScreen(
                            initialStatusType = uiState.statusType,
                            initialLargeCategory = largeCategory
                        )
                    )
                }
            )

            ExpensesLineChart(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp),
                statusType = uiState.statusType,
                currentData = uiState.currentExpensesData,
                lastData = uiState.lastExpensesData
            )

            CategorySegmentedChart(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp),
                statusType = uiState.statusType,
                expensesAmount = uiState.currentAmount?.expensesAmount.default(),
                categorySegmentChartItems = uiState.categorySegmentChartItems,
                onCategoryChartClick = {
                    // 카테고리 차트 전체 클릭 시 지출 현황 화면으로 이동 (비교 섹션으로 스크롤)
                    navigator.push(
                        PreparednessStatusScreen(
                            initialStatusType = uiState.statusType,
                            initialLargeCategory = LargeCategoryEnum.EXPENSES,
                            scrollToPosition = 2
                        )
                    )
                },
                onCategoryItemClick = { categoryId ->
                    // 개별 카테고리 클릭 시 상세 지출 내역 화면으로 이동
                    navigator.push(
                        CategoryExpensesScreen(
                            initialStatusType = uiState.statusType,
                            initialLargeCategory = LargeCategoryEnum.EXPENSES,
                            categoryId = categoryId
                        )
                    )
                }
            )

            PaymentMethodSegmentedChart(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp),
                statusType = uiState.statusType,
                expensesAmount = uiState.currentAmount?.expensesAmount.default(),
                paymentMethodSegmentChartItems = uiState.paymentMethodSegmentChartItems,
                onPaymentMethodChartClick = {
                    // 카테고리 차트 전체 클릭 시 지출 현황 화면으로 이동
                    navigator.push(
                        PreparednessStatusScreen(
                            initialStatusType = uiState.statusType,
                            initialLargeCategory = LargeCategoryEnum.EXPENSES,
                            scrollToPosition = 5
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        if (isShowStatusTypeModalBottomSheet) {
            WMListSelectionModalBottomSheet(
                title = "기간 선택",
                items = StatusType.entries,
                selectedItem = uiState.statusType,
                itemLabel = { it.label },
                onItemSelected = { screenModel.updateStatusType(it) },
                onDismissRequest = { isShowStatusTypeModalBottomSheet = false }
            )
        }
    }
}
