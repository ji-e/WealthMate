package com.jie.wealthmate.feature.budget.budgetYearDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun BudgetYearsHeader(
    year: String,
    actualIncome: Long,
    lastActualIncome: Long,
    actualExpense: Long,
    lastActualExpense: Long,
    actualSaving: Long,
    lastActualSaving: Long,
    modifier: Modifier = Modifier,
) {
    val summary = remember(
        actualIncome,
        actualExpense,
        actualSaving,
        lastActualIncome,
        lastActualExpense,
        lastActualSaving
    ) {
        object {
            val actualRemain = actualIncome - actualExpense - actualSaving
            val lastActualRemain = lastActualIncome - lastActualExpense - lastActualSaving
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WMText(
            text = "${year}년 재정 요약",
            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        SummaryRow(
            label = "총 수입",
            actualAmount = actualIncome,
            lastActualAmount = lastActualIncome,
        )
        SummaryRow(
            label = "총 지출",
            actualAmount = actualExpense,
            lastActualAmount = lastActualExpense,
            isInverseColor = true
        )
        SummaryRow(
            label = "총 저축",
            actualAmount = actualSaving,
            lastActualAmount = lastActualSaving,
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = ColorGray.Gray_200
        )

        SummaryRow(
            label = "잔액",
            actualAmount = summary.actualRemain,
            lastActualAmount = summary.lastActualRemain,
            color = if (summary.actualRemain >= 0) ColorGray.Gray_700 else ColorRed.Red_300,
            isBold = true
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    actualAmount: Long,
    lastActualAmount: Long,
    color: Color = ColorGray.Gray_700,
    isBold: Boolean = false,
    isInverseColor: Boolean = false,
) {
    val percentageData = remember(actualAmount, lastActualAmount, isInverseColor) {
        if (lastActualAmount == 0L) return@remember null

        val percentage =
            (((actualAmount - lastActualAmount).toFloat() / lastActualAmount.absoluteValue) * 100f).roundToInt()

        val textColor = when {
            percentage == 0 -> ColorGray.Gray_400
            isInverseColor -> if (percentage > 0) ColorRed.Red_300 else ColorBlue.Blue_300
            else -> if (percentage > 0) ColorBlue.Blue_300 else ColorRed.Red_300
        }

        val sign = if (percentage > 0) "+" else ""
        val displayText = if (percentage == 0) "지난해와 동일" else "$sign$percentage%"

        displayText to textColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = label,
            style = typography.titleSmall.copy(
                color = ColorGray.Gray_700,
                fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Medium
            ),
        )

        percentageData?.let { (text, textColor) ->
            WMText(
                text = text,
                style = typography.bodySmall.copy(color = textColor),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        WMText(
            text = "${actualAmount.formatWithCommas()}원",
            style = typography.titleMedium.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
