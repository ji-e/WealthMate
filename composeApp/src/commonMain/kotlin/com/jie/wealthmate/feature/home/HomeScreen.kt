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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.feature.home.component.CategorySegmentedChart
import com.jie.wealthmate.feature.home.component.ExpensesLineChart
import com.jie.wealthmate.feature.home.component.LargeCategoryStatus
import com.jie.wealthmate.feature.home.component.PaymentMethodSegmentedChart
import com.jie.wealthmate.feature.home.component.RemainBudget
import com.jie.wealthmate.feature.home.component.Today
import com.jie.wealthmate.theme.ColorGray
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
                budgetAmount = uiState.budgetAmount,
                expensesAmount = uiState.currentAmount?.expensesAmount.default()
            )

            if (uiState.budgetAmount > 0L) {
                RemainBudget(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(top = 36.dp),
                    budgetAmount = uiState.budgetAmount,
                    expensesAmount = uiState.currentAmount?.expensesAmount.default()
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
                    .padding(top = 24.dp),
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
                onCategoryClick = {
                    // todo 카테고리 클릭 시 이동
                }
            )

            ExpensesLineChart(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp),
                statusType = uiState.statusType,
                currentData = List(31) { if (it < 12) (it + (it % 7)) / 100f else null },
                lastData = List(31) { (it + (it % 9)) / 100f }
            )

            CategorySegmentedChart(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp),
                statusType = uiState.statusType,
                expensesAmount = uiState.currentAmount?.expensesAmount.default(),
                categorySegmentChartItems = uiState.categorySegmentChartItems,
                onCategoryChartClick = {
                    // todo 카테고리 차트 클릭 시 이동
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
                    // todo 결제수단 차트 클릭 시 이동
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}