package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

@Composable
fun RemainBudget(
    modifier: Modifier = Modifier,
    budgetAmount: Long,
    expensesAmount: Long,
) {
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        WMText(
            text = "이번 달 남은 금액",
            style = typography.titleSmall.copy(color = ColorGray.Gray_500),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        AnimatedBudgetBar(spent = expensesAmount, total = budgetAmount)
    }
}

@Composable
private fun AnimatedBudgetBar(spent: Long, total: Long) {
    val typography = MaterialTheme.typography
    var isStarted by remember { mutableStateOf(false) }

    // spent 값을 직접 애니메이션
    val animatedSpent by animateFloatAsState(
        targetValue = if (isStarted) spent.toFloat() else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "SpentAnimation"
    )

    // 남은 금액 계산 (음수 가능)
    val currentRemaining = total - animatedSpent

    // 바 그래프용 비율 계산 (0.0 ~ 1.0으로 제한 및 0 나누기 방지)
    val barRatio = if (total > 0) (animatedSpent / total).coerceIn(0f, 1f) else 0f

    // 색상 애니메이션 (80% 이상 사용 시 경고 색상)
    val animatedColor by animateColorAsState(
        targetValue = if (barRatio > 0.8f) ColorRed.Red_300 else ColorBlue.Blue_200,
        animationSpec = tween(500),
        label = "ColorAnimation"
    )

    LaunchedEffect(Unit) {
        isStarted = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.padding(bottom = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            WMText(
                text = "${formatWithCommas(currentRemaining.roundToLong().toString())}원",
                style = typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
            )

            val percentageText = remember(currentRemaining, total) {
                if (total > 0) {
                    val ratio = (currentRemaining.absoluteValue / total.toFloat() * 100).toInt()
                    if (currentRemaining >= 0) "총 $ratio% 남음" else "총 $ratio% 초과"
                } else {
                    "0%"
                }
            }

            WMText(
                text = percentageText,
                style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        // 그래프 배경
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_200)
        ) {
            // 차오르는 바
            Box(
                modifier = Modifier
                    .fillMaxWidth(barRatio)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(animatedColor)
            )
        }
    }
}