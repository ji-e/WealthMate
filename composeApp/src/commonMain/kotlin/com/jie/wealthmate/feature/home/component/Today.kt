package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.today
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlin.math.absoluteValue

@Composable
fun Today(
    modifier: Modifier = Modifier,
    todayAmount: Long,
    budgetAmount: Long,
    expensesAmount: Long,
) {
    val typography = MaterialTheme.typography

    val remainBudget = budgetAmount - expensesAmount
    val dailyInsight = remember(remainBudget, todayAmount) {
        calculateDailyInsight(
            remainingBudget = remainBudget,
            todayAmount = todayAmount
        )
    }
    val (titleText, amount, infoText) = remember(
        budgetAmount,
        remainBudget,
        todayAmount,
        dailyInsight
    ) {
        if (budgetAmount > 0) {
            if (remainBudget < 0) {
                Triple(
                    "이미 예산 초과! 오늘 지출 금액은",
                    todayAmount,
                    "예산을 다 사용했어요!\n남은 ${dailyInsight.remainingDays}일 절약모드 ON 🚨"
                )
            } else {
                Triple(
                    "오늘 남은 금액은",
                    dailyInsight.remainDailyAmount,
                    dailyInsight.budgetStatusMessage
                )
            }
        } else {
            Triple(
                "오늘 지출 금액은",
                todayAmount,
                dailyInsight.statusMessage
            )
        }
    }

    Column(modifier = modifier) {
        WMText(
            text = titleText,
            style = typography.titleSmall.copy(color = ColorGray.Gray_500),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                WMText(
                    text = "${formatWithCommas(amount.toString())}원",
                    style = typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                WMText(
                    text = infoText,
                    style = typography.bodyMedium,
                )

                if (budgetAmount > 0 && remainBudget > 0) {
                    WMText(
                        text = "오늘 지출 금액: ${formatWithCommas(todayAmount.toString())}원",
                        style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (budgetAmount > 0) {
                AnimatedDonutChart(
                    total = dailyInsight.dailyLimit.toFloat(),
                    chartData = todayAmount.toFloat(),
                    isRemainBudgetZero = remainBudget <= 0
                )
            }
        }
    }
}

@Composable
private fun AnimatedDonutChart(
    total: Float,
    chartData: Float,
    modifier: Modifier = Modifier,
    isRemainBudgetZero: Boolean,
) {
    val typography = MaterialTheme.typography

    val strokeWidth = 16.dp
    val animatedProgress = remember { Animatable(0f) }
    val remainingPercentage =
        if (isRemainBudgetZero || total <= 0f) 0f
        else ((total - chartData) / total) * 100f
    val isLowBudget = remainingPercentage < 10f
    val remainingPercentageText = remember(isRemainBudgetZero, remainingPercentage) {
        when {
            isRemainBudgetZero -> "예산\n초과"
            remainingPercentage > 0 -> "${remainingPercentage.toInt()}%\n남음"
            else -> "${remainingPercentage.toInt().absoluteValue}%\n초과"
        }
    }
    val animatedColor by animateColorAsState(
        targetValue = if (isLowBudget) ColorRed.Red_300 else ColorBlue.Blue_200,
        animationSpec = tween(500),
        label = "ChartColorAnimation"
    )

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.minDimension
            val radius = (canvasSize - strokeWidth.toPx()) / 2
            val centerX = size.width / 2
            val centerY = size.height / 2

            val startAngle = -90f
            // total이 0일 경우 대비
            val sweepAngle = if (total > 0) (chartData / total) * 360f else 360f
            val animatedSweepAngle = -sweepAngle * animatedProgress.value

            drawArc(
                color = animatedColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
            )

            drawArc(
                color = ColorGray.Gray_200,
                startAngle = startAngle,
                sweepAngle = animatedSweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
            )
        }

        WMText(
            text = remainingPercentageText,
            style = typography.labelSmall.copy(
                color = ColorGray.Gray_500,
                lineHeight = 14.sp
            ),
            textAlign = TextAlign.Center,
        )
    }
}

private fun calculateDailyInsight(
    remainingBudget: Long,
    todayAmount: Long,
): DailyInsight {
    val nextMonth = if (today.month.number == 12) 1 else today.month.number + 1
    val nextMonthYear = if (today.month.number == 12) today.year + 1 else today.year
    val firstDayOfNextMonth = LocalDate(nextMonthYear, nextMonth, 1)
    val lastDayOfMonth = firstDayOfNextMonth.minus(1, DateTimeUnit.DAY)

    val remainingDays = (lastDayOfMonth.day - today.day + 1).coerceAtLeast(1)
    val dailyLimit = remainingBudget / remainingDays
    val remainDailyAmount = dailyLimit - todayAmount

    val budgetStatusMessage = when {
        remainDailyAmount > 100000 -> "여유로워요! 맛있는 걸 먹을 수 있어요. 🍕"
        remainDailyAmount in 30000..100000 -> "좋은 페이스예요! 이대로 유지하세요. 😊"
        remainDailyAmount in 10000..29999 -> "지갑이 가벼워지고 있어요. ⚠️"
        else -> "다 썼어요. 내일을 위해 지갑을 닫아요. 🚨"
    }

    val statusMessage = when {
        todayAmount > 100000 -> "오늘 큰 지출이 있었네요.\n내일은 조금 여유롭게 보내볼까요? 💸"
        todayAmount in 50000..100000 -> "평범한 소비의 하루네요.\n나쁘지 않아요. ☕"
        todayAmount in 10000..49999 -> "오늘 알뜰하게 보내셨네요.\n지갑이 든든해요. 👛"
        todayAmount > 0 -> "거의 쓰지 않으셨네요!\n절약의 하루예요. 🌟"
        else -> "지출 없는 하루예요.\n완벽한 무지출 챌린지! 🎯"
    }

    return DailyInsight(
        dailyLimit = dailyLimit,
        remainDailyAmount = remainDailyAmount,
        remainingDays = remainingDays,
        budgetStatusMessage = budgetStatusMessage,
        statusMessage = statusMessage
    )
}

private data class DailyInsight(
    val dailyLimit: Long,
    val remainDailyAmount: Long,
    val remainingDays: Int,
    val budgetStatusMessage: String,
    val statusMessage: String,
)