package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmptyBoxView
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.component.vo.PaymentMethodSegmentedChartVo
import com.jie.wealthmate.theme.ColorChart
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.PaymentMethodVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right


@Composable
fun PaymentMethodSegmentedChart(
    statusType: StatusType,
    modifier: Modifier = Modifier,
    expensesAmount: Long,
    paymentMethodSegmentChartItems: List<PaymentMethodSegmentedChartVo>,
    onPaymentMethodChartClick: () -> Unit = {},
    onPaymentMethodItemClick: (String?) -> Unit = {},
) {
    val typography = MaterialTheme.typography
    var isStarted by remember { mutableStateOf(false) }

    // 1. 전체 데이터 구성 (미지정 항목 포함 및 금액 내림차순 정렬)
    val displayItems = remember(paymentMethodSegmentChartItems, expensesAmount) {
        val assignedSum = paymentMethodSegmentChartItems.sumOf { it.amount }
        val unassignedAmount = (expensesAmount - assignedSum).coerceAtLeast(0L)

        val allItems = if (unassignedAmount > 0) {
            paymentMethodSegmentChartItems + PaymentMethodSegmentedChartVo(
                paymentMethod = null, // null을 결제수단 없음으로 처리
                amount = unassignedAmount
            )
        } else {
            paymentMethodSegmentChartItems
        }

        allItems.sortedByDescending { it.amount }
    }

    // 2. 분모는 expensesAmount와 실제 항목 합 중 큰 값을 사용하여 100%를 초과하지 않도록 함
    val totalAmountForCalc = remember(displayItems, expensesAmount) {
        displayItems.sumOf { it.amount }.coerceAtLeast(expensesAmount).coerceAtLeast(1L)
    }

    val colors = remember { ColorChart.getPaymentMethodChartColors() }

    val animProgress by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "ChartAnimation"
    )

    LaunchedEffect(Unit) { isStarted = true }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = Padding.BackgroundHorizontal)
                .noRippleClickable(onClick = onPaymentMethodChartClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "${statusType.label} 결제수단별 지출",
                style = typography.titleSmall,
                color = ColorSetting.Info,
            )

            Icon(
                painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                contentDescription = null,
                tint = ColorSetting.Info,
                modifier = Modifier.size(16.dp)
            )
        }

        WMSpacer(size = SpacerSize.SMALL)

        if (displayItems.isEmpty()) {
            EmptyBoxView(
                modifier = Modifier.padding(horizontal = Padding.BackgroundHorizontal),
                contentText = "결제 내역이 없습니다.",
            )
        } else {
            // 차트 영역
            Row(
                modifier = Modifier
                    .padding(horizontal = Padding.BackgroundHorizontal)
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(Shapes.medium)
            ) {
                displayItems.forEachIndexed { index, data ->
                    val proportion = data.amount.toFloat() / totalAmountForCalc.toFloat()
                    val currentWeight = (proportion * animProgress).coerceAtLeast(0.0001f)
                    val color = colors[index % colors.size]

                    Box(
                        modifier = Modifier
                            .weight(currentWeight)
                            .fillMaxHeight()
                            .background(color)
                    )
                }

                if (animProgress < 1f) {
                    val remainingWeight = (1f - animProgress).coerceAtLeast(0.0001f)
                    Box(
                        modifier = Modifier
                            .weight(remainingWeight)
                            .fillMaxHeight()
                    )
                }
            }

            WMSpacer(size = SpacerSize.X_SMALL)

            // 리스트 영역
            displayItems.forEachIndexed { index, item ->
                val color = colors[index % colors.size]
                SegmentedItem(
                    icon = "", // 결제수단은 현재 아이콘이 없으므로 null 처리
                    label = (item.paymentMethod?.label ?: "결제수단 없음"),
                    color = color,
                    totalAmount = totalAmountForCalc,
                    amount = item.amount,
                    onItemClick = {
                        onPaymentMethodItemClick(item.paymentMethod?.id.default())
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PaymentMethodSegmentedChartPreview() {
    val mockItems = listOf(
        PaymentMethodSegmentedChartVo(
            paymentMethod = PaymentMethodVo(
                id = "1",
                label = "신한카드",
                groupId = null,
                groupLabel = "신용카드",
                sort = 1
            ),
            amount = 600000
        ),
        PaymentMethodSegmentedChartVo(
            paymentMethod = PaymentMethodVo(
                id = "2",
                label = "현금",
                groupId = null,
                groupLabel = null,
                sort = 2
            ),
            amount = 300000
        ),
        PaymentMethodSegmentedChartVo(
            paymentMethod = PaymentMethodVo(
                id = "3",
                label = "카카오페이 아주긴결제수단이름테스트",
                groupId = null,
                groupLabel = null,
                sort = 3
            ),
            amount = 100000
        )
    )

    WMTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 16.dp)
        ) {
            PaymentMethodSegmentedChart(
                statusType = StatusType.MONTH,
                expensesAmount = 1000000,
                paymentMethodSegmentChartItems = mockItems,
            )
        }
    }
}

@Preview
@Composable
private fun PaymentMethodSegmentedChartEmptyPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 16.dp)
        ) {
            PaymentMethodSegmentedChart(
                statusType = StatusType.MONTH,
                expensesAmount = 0,
                paymentMethodSegmentChartItems = emptyList(),
            )
        }
    }
}
