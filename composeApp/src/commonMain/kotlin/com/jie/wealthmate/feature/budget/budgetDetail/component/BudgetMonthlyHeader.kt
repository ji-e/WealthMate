package com.jie.wealthmate.feature.budget.budgetDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.SummaryRow
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed

@Composable
fun BudgetMonthlyHeader(
    actualIncome: Long,
    lastActualIncome: Long,
    actualExpense: Long,
    lastActualExpense: Long,
    actualSaving: Long,
    lastActualSaving: Long,
    modifier: Modifier = Modifier,
) {
    val remain = actualIncome - actualExpense - actualSaving
    val lastRemain = lastActualIncome - lastActualExpense - lastActualSaving

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WMText(
            text = "재정 요약",
            style = typography.titleSmall.copy(
                color = ColorGray.Gray_500,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        SummaryRow(
            type = "지난달과",
            label = "총 수입",
            actualAmount = actualIncome,
            lastActualAmount = lastActualIncome
        )
        SummaryRow(
            type = "지난달과",
            label = "총 지출",
            actualAmount = actualExpense,
            lastActualAmount = lastActualExpense,
            isInverseColor = true
        )
        SummaryRow(
            type = "지난달과",
            label = "총 저축",
            actualAmount = actualSaving,
            lastActualAmount = lastActualSaving
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = ColorGray.Gray_200
        )

        SummaryRow(
            type = "지난달과",
            label = "잔액",
            actualAmount = remain,
            lastActualAmount = lastRemain,
            color = if (remain >= 0) ColorGray.Gray_700 else ColorRed.Red_300,
            isBold = true
        )
    }
}
