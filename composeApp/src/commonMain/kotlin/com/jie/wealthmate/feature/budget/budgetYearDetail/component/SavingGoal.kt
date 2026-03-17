package com.jie.wealthmate.feature.budget.budgetYearDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun SavingGoal(
    budgetSaving: Long,
    actualSaving: Long,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val isBudgetEmpty = budgetSaving == 0L
    val isGoalReached = budgetSaving in 1..actualSaving

    val (progress, percentageText) = remember(budgetSaving, actualSaving) {
        if (isBudgetEmpty) {
            0f to "-"
        } else {
            val p = (actualSaving.toFloat() / budgetSaving).coerceIn(0f, 1f)
            p to "${(p * 100).toInt()}"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WMText(
            text = "저축 목표 달성률 $percentageText%",
            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = if (progress >= 0.8f) ColorPrimary.Primary_500 else ColorPrimary.Primary_200,
            trackColor = if (isBudgetEmpty) ColorGray.Gray_50 else ColorGray.Gray_100,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                WMText(
                    text = "현재 저축액",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500)
                )
                WMText(
                    text = "${actualSaving.formatWithCommas()}원",
                    style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val labelColor = if (isBudgetEmpty) ColorGray.Gray_100 else ColorGray.Gray_500
                val valueColor = if (isBudgetEmpty) ColorGray.Gray_100 else ColorGray.Gray_700

                WMText(
                    text = "목표 저축액",
                    style = typography.bodySmall.copy(color = labelColor)
                )
                WMText(
                    text = "${budgetSaving.formatWithCommas()}원",
                    style = typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = valueColor
                    )
                )
            }
        }

        if (isGoalReached) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorGray.Gray_50)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = "🎉 목표 저축액을 달성했어요!",
                    style = typography.bodyMedium
                )
            }
        }
    }
}
