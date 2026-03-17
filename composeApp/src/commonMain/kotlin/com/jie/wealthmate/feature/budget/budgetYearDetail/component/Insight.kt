package com.jie.wealthmate.feature.budget.budgetYearDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.budgetYearDetail.MonthlyComparison
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun Insight(
    totalIncome: Long,
    totalExpense: Long,
    totalSaving: Long,
    monthlyData: List<MonthlyComparison> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    
    val insightData = remember(totalIncome, totalExpense, totalSaving, monthlyData) {
        val totalActual = totalIncome - totalExpense - totalSaving
        val expenseRatio = if (totalIncome > 0) (totalExpense.toFloat() / totalIncome * 100).toInt() else -1
        val savingRatio = if (totalIncome > 0) (totalSaving.toFloat() / totalIncome * 100).toInt() else -1
        
        val maxExpenseMonth = monthlyData.filter { it.expense > 0 }.maxByOrNull { it.expense }
        val maxSavingMonth = monthlyData.filter { it.saving > 0 }.maxByOrNull { it.saving }
        
        InsightState(
            totalActual = totalActual,
            expenseRatio = expenseRatio,
            savingRatio = savingRatio,
            maxExpenseMonth = maxExpenseMonth,
            maxSavingMonth = maxSavingMonth
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WMText(
            text = "인사이트",
            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        InsightItem(
            icon = "💡",
            title = "소득 대비 지출 비율",
            content = if (insightData.expenseRatio >= 0) "총 수입의 ${insightData.expenseRatio}%를 지출하고 있어요."
            else "데이터가 부족해요."
        )

        InsightItem(
            icon = "💰",
            title = "저축 성적",
            content = if (insightData.savingRatio >= 0) "총 수입의 ${insightData.savingRatio}%를 저축했어요."
            else "데이터가 부족해요."
        )

        insightData.maxExpenseMonth?.let {
            InsightItem(
                icon = "📉",
                title = "최다 지출 월",
                content = "${it.month}월에 가장 많은 지출(${it.expense.formatWithCommas()}원)이 있었어요."
            )
        }

        insightData.maxSavingMonth?.let {
            InsightItem(
                icon = "🏆",
                title = "최다 저축 월",
                content = "${it.month}월에 가장 많은 저축(${it.saving.formatWithCommas()}원)을 달성했어요!"
            )
        }

        InsightItem(
            icon = "⚖️",
            title = "여유 자금",
            content = if (insightData.totalActual >= 0) {
                "연간 총 ${insightData.totalActual.formatWithCommas()}원의 잔액이 남았어요."
            } else {
                "연간 총 ${(-insightData.totalActual).formatWithCommas()}원의 적자가 발생했어요."
            }
        )
    }
}

private data class InsightState(
    val totalActual: Long,
    val expenseRatio: Int,
    val savingRatio: Int,
    val maxExpenseMonth: MonthlyComparison?,
    val maxSavingMonth: MonthlyComparison?
)

@Composable
private fun InsightItem(
    icon: String,
    title: String,
    content: String,
) {
    val typography = MaterialTheme.typography
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.Gray_50)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = icon,
            style = typography.headlineSmall,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.Center) {
            WMText(
                text = title,
                style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            WMText(
                text = content,
                style = typography.bodyMedium.copy(color = ColorGray.Gray_500)
            )
        }
    }
}
