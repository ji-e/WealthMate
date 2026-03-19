package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.component.vo.DailyInsightVo
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.utils.lastDayOfMonth
import com.jie.wealthmate.utils.today
import kotlinx.datetime.LocalDate

@Composable
fun Today(
    todayAmount: Long,
    budgetAmount: Long,
    expensesAmount: Long,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography

    val remainBudget = budgetAmount - expensesAmount
    val dailyInsight = remember(remainBudget, todayAmount) {
        calculateDailyInsight(
            remainingBudget = remainBudget,
            todayAmount = todayAmount,
            now = today
        )
    }

    val (titleText, displayAmount, infoText) = remember(
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

    Column(modifier = modifier.fillMaxWidth()) {
        WMText(
            text = titleText,
            style = typography.titleSmall,
            color = ColorSetting.Info,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                WMText(
                    text = "${displayAmount.formatWithCommas()}원",
                    style = typography.displaySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                WMText(
                    text = infoText,
                )

                if (budgetAmount > 0 && remainBudget > 0) {
                    WMText(
                        text = "오늘 지출 금액: ${todayAmount.formatWithCommas()}원",
                        style = typography.bodySmall,
                        color = ColorSetting.Info,
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
    val strokeWidth = 12.dp
    val animatedProgress = remember { Animatable(0f) }

    val remainingPercentage = remember(isRemainBudgetZero, total, chartData) {
        if (isRemainBudgetZero || total <= 0f) 0f
        else ((total - chartData) / total * 100f).coerceIn(0f, 100f)
    }

    val remainingPercentageText = remember(isRemainBudgetZero, remainingPercentage) {
        when {
            isRemainBudgetZero -> "예산\n초과"
            else -> "${remainingPercentage.toInt()}%\n남음"
        }
    }

    val animatedColor by animateColorAsState(
        targetValue = if (remainingPercentage < 10f || isRemainBudgetZero) ColorRed.Red_300 else ColorBlue.Blue_200,
        animationSpec = tween(500),
        label = "ChartColorAnimation"
    )

    LaunchedEffect(chartData, total) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Box(
        modifier = modifier.size(84.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
            val startAngle = -90f
            val sweepAngle = if (total > 0) (chartData / total).coerceIn(0f, 1f) * 360f else 360f
            val animatedSweepAngle = -sweepAngle * animatedProgress.value

            // 배경 원
            drawArc(
                color = animatedColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // 지출 표시 (어두운 회색으로 덮어씀)
            drawArc(
                color = ColorGray.Gray_200,
                startAngle = startAngle,
                sweepAngle = animatedSweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        WMText(
            text = remainingPercentageText,
            style = typography.labelSmall.copy(lineHeight = 14.sp),
            color = ColorSetting.Info,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

private fun calculateDailyInsight(
    remainingBudget: Long,
    todayAmount: Long,
    now: LocalDate,
): DailyInsightVo {
    val lastDayOfMonth = now.lastDayOfMonth()
    val remainingDays = (lastDayOfMonth.day - now.day + 1).coerceAtLeast(1)
    val dailyLimit = (remainingBudget / remainingDays).coerceAtLeast(0L)
    val remainDailyAmount = dailyLimit - todayAmount

    val budgetStatusMessage = when {
        remainDailyAmount > 100000 -> "여유로워요! 맛있는 걸 먹을 수 있어요. 🍕"
        remainDailyAmount in 30000..100000 -> "좋은 페이스예요! 이대로 유지하세요. 😊"
        remainDailyAmount in 1..29999 -> "지갑이 가벼워지고 있어요. ⚠️"
        else -> "다 썼어요. 내일을 위해 지갑을 닫아요. 🚨"
    }

    val statusMessage = when {
        todayAmount > 100000 -> "오늘 큰 지출이 있었네요.\n내일은 조금 여유롭게 보내볼까요? 💸"
        todayAmount in 50000..100000 -> "평범한 소비의 하루네요.\n나쁘지 않아요. ☕"
        todayAmount in 10000..49999 -> "오늘 알뜰하게 보내셨네요.\n지갑이 든든해요. 👛"
        todayAmount > 0 -> "거의 쓰지 않으셨네요!\n절약의 하루예요. 🌟"
        else -> "지출 없는 하루예요.\n완벽한 무지출 챌린지! 🎯"
    }

    return DailyInsightVo(
        dailyLimit = dailyLimit,
        remainDailyAmount = remainDailyAmount,
        remainingDays = remainingDays,
        budgetStatusMessage = budgetStatusMessage,
        statusMessage = statusMessage
    )
}


@Preview
@Composable
private fun TodayPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 예산 설정된 경우 - 여유
            Today(
                todayAmount = 0,
                budgetAmount = 500000,
                expensesAmount = 200000
            )

            // 예산 설정된 경우 - 초과 임박
            Today(
                todayAmount = 23000,
                budgetAmount = 500000,
                expensesAmount = 200000
            )

            // 예산 설정된 경우 - 예산 초과
            Today(
                todayAmount = 30000,
                budgetAmount = 500000,
                expensesAmount = 200000
            )

            // 예산 미설정
            Today(
                todayAmount = 25000,
                budgetAmount = 0,
                expensesAmount = 200000
            )
        }
    }
}
