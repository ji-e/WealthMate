package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.theme.ColorChart
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.PaymentMethodVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right


@Composable
fun PaymentMethodSegmentedChart(
    statusType: StatusType,
    modifier: Modifier = Modifier,
    expensesAmount: Long,
    paymentMethodSegmentChartItems: List<PaymentMethodSegmentChartData>,
    onPaymentMethodChartClick: () -> Unit = {},
) {
    val typography = MaterialTheme.typography
    var isStarted by remember { mutableStateOf(false) }
    val totalAmount = expensesAmount.toFloat()
    val colors = remember { ColorChart.getPaymentMethodChartColors() }

    // 전체 애니메이션 진행도 (0.0 -> 1.0)
    val animProgress by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "ChartAnimation"
    )

    LaunchedEffect(Unit) { isStarted = true }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clickable(onClick = onPaymentMethodChartClick)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "${statusType.label} 결제수단별 지출",
                style = typography.titleSmall.copy(color = ColorGray.Gray_500),
            )
            Icon(
                painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                contentDescription = null,
                tint = ColorGray.Gray_500,
                modifier = Modifier.size(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
        ) {
            if (totalAmount > 0) {
                paymentMethodSegmentChartItems.forEachIndexed { index, data ->
                    val proportion = data.amount / totalAmount
                    val currentWeight = proportion * animProgress

                    if (currentWeight > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(currentWeight)
                                .fillMaxHeight()
                                .background(colors[index % colors.size])
                        )
                    }
                }

                if (animProgress < 1f) {
                    Box(
                        modifier = Modifier
                            .weight((1f - animProgress).coerceAtLeast(0.001f))
                            .fillMaxHeight()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        paymentMethodSegmentChartItems.forEachIndexed { index, item ->
            PaymentMethodSegmentedItem(
                expensesAmount = expensesAmount,
                paymentMethodSegment = item,
                color = colors[index % colors.size]
            )
        }
    }
}

@Composable
private fun PaymentMethodSegmentedItem(
    expensesAmount: Long,
    paymentMethodSegment: PaymentMethodSegmentChartData,
    color: Color,
) {
    val typography = MaterialTheme.typography
    val paymentMethod = paymentMethodSegment.paymentMethod
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color)
                .size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // 결제수단 아이콘이 있다면 여기에 추가 가능
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                WMText(
                    text = paymentMethod.label,
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                )

                val rate = remember(expensesAmount, paymentMethodSegment.amount) {
                    calculateRate(expensesAmount, paymentMethodSegment.amount)
                }

                WMText(
                    text = "${rate}%",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            WMText(
                text = "${formatWithCommas(paymentMethodSegment.amount.toString())}원",
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1
            )
        }
    }
}

data class PaymentMethodSegmentChartData(
    val paymentMethod: PaymentMethodVo,
    val amount: Long,
)
