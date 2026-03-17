package com.jie.wealthmate.feature.budget.budgetDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun BudgetMonthlyHeader(
    income: Long,
    expense: Long,
    saving: Long,
    modifier: Modifier = Modifier,
) {
    val remain = income - expense - saving

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.Gray_50, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        SummaryRow(label = "총 수입", amount = income)
        SummaryRow(label = "총 지출", amount = expense)
        SummaryRow(label = "총 저축", amount = saving)

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = ColorGray.Gray_200)

        SummaryRow(
            label = "잔액",
            amount = income - expense - saving,
            color = if (remain >= 0) ColorGray.Gray_700 else ColorRed.Red_300,
            isBold = true
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: Long,
    color: Color = ColorGray.Gray_700,
    isBold: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = label,
            style = typography.titleSmall.copy(color = ColorGray.Gray_500),
        )
        WMText(
            text = "${amount.formatWithCommas()}원",
            style = typography.titleMedium.copy(
                color = color,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold
            )
        )
    }
}

