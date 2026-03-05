package com.jie.wealthmate.feature.budget.budgetSetting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.budgetSetting.YearlySummary
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun YearBudget(
    modifier: Modifier = Modifier,
    year: String,
    summary: YearlySummary,
) {
    Column(
        modifier = modifier
            .padding(top = 12.dp)
            .background(ColorGray.Gray_50)
            .padding(horizontal = 32.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        LabelText(text = "${year}년 요약")

        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                label = "총 수입",
                value = summary.actualIncome,
                valueColor = ColorBlue.Blue_300,
                bottomLabel = "목표",
                bottomValue = "${formatWithCommas(summary.targetIncome.toString())}원"
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                label = "총 저축",
                value = summary.actualSaving,
                valueColor = ColorPrimary.Primary_500,
                bottomLabel = "목표",
                bottomValue = "${formatWithCommas(summary.targetSaving.toString())}원"
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                label = "총 지출",
                value = summary.actualExpense,
                valueColor = ColorRed.Red_300,
                bottomLabel = "목표",
                bottomValue = "${formatWithCommas(summary.targetExpense.toString())}원"
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                label = "수입 - 지출",
                value = summary.balance,
                bottomLabel = "지출률",
                bottomValue = summary.expenseRate?.let { "$it%" } ?: "-%"
            )
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    value: Long,
    valueColor: Color = Color.Unspecified,
    bottomLabel: String,
    bottomValue: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.White)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        WMText(
            text = label,
            style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        )
        WMText(
            text = "${formatWithCommas(value.toString())}원",
            style = typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 9.sp,
                maxFontSize = 22.sp,
                stepSize = 1.sp
            )
        )
        Row {
            WMText(
                text = bottomLabel,
                style = typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            )
            WMText(
                text = " $bottomValue",
                style = typography.bodySmall,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 9.sp,
                    maxFontSize = 12.sp,
                    stepSize = 1.sp
                )
            )
        }
    }
}
