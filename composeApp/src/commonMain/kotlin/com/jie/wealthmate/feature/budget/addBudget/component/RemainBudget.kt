package com.jie.wealthmate.feature.budget.addBudget.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun RemainBudget(
    modifier: Modifier = Modifier,
    selectedLargeCategory: LargeCategoryEnum,
    totalBudget: Long,
    remainBudget: Long,
    allocatedAmount: Long,
) {
    val progress = remember(totalBudget, allocatedAmount) {
        if (totalBudget > 0) (allocatedAmount.toFloat() / totalBudget).coerceIn(0f, 1.1f) else 0f
    }
    val usagePercentage = (progress * 100).toInt()
    val isOverBudget = remainBudget < 0

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtMost(1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "BudgetProgress"
    )

    val progressBarColor by animateColorAsState(
        targetValue = if (isOverBudget) ColorRed.Red_300 else ColorPrimary.Primary_500,
        label = "ProgressBarColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColorGray.Gray_50)
            .padding(16.dp)
    ) {
        // 할당된 금액 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "할당된 ${selectedLargeCategory.label}",
                style = typography.titleSmall.copy(color = ColorGray.Gray_500),
                modifier = Modifier.weight(1f),
            )

            WMText(
                text = "${allocatedAmount.formatWithCommas()}원",
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 남은 예산 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = if (isOverBudget) "예산 초과" else "남은 예산",
                style = typography.titleSmall.copy(color = ColorGray.Gray_500),
                modifier = Modifier.weight(1f),
            )

            WMText(
                text = "${remainBudget.formatWithCommas()}원",
                style = typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isOverBudget) ColorRed.Red_300 else ColorPrimary.Primary_600,
                ),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 프로그레스 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(ColorGray.Gray_200)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(progressBarColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 퍼센트 표시
        WMText(
            text = "$usagePercentage% 할당됨",
            modifier = Modifier.align(Alignment.End),
            style = typography.bodySmall.copy(
                color = if (isOverBudget) ColorRed.Red_300 else ColorGray.Gray_500,
                fontWeight = if (isOverBudget) FontWeight.Bold else FontWeight.Normal
            ),
        )
    }
}
