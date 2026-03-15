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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.jie.wealthmate.feature.budget.budgetYearDetail.FixedExpense
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun ExpenseFixed(
    year: String,
    fixedExpenses: List<FixedExpense>,
    totalExpense: Long,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography

    // 계산 로직을 remember로 감싸서 불필요한 재계산 방지
    val totalFixedAmount = remember(fixedExpenses) {
        fixedExpenses.sumOf { it.amount }
    }
    
    val percentage = remember(totalFixedAmount, totalExpense) {
        if (totalExpense > 0) {
            (totalFixedAmount.toDouble() / totalExpense.toDouble() * 100).toInt()
        } else {
            0
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WMText(
            text = "${year}년 고정지출",
            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WMText(
                text = "총 ${totalFixedAmount.formatWithCommas()}원",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            if (totalExpense > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                WMText(
                    text = "전체 지출의 ${percentage}%",
                    style = typography.labelSmall.copy(color = ColorGray.Gray_500)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (fixedExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = "등록된 고정 지출이 없어요.",
                    style = typography.bodyMedium.copy(color = ColorGray.Gray_400)
                )
            }
        } else {
            // Arrangement.spacedBy를 사용하여 간격 설정 최적화
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fixedExpenses.forEach { expense ->
                    FixedExpenseItem(expense = expense)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun FixedExpenseItem(
    expense: FixedExpense,
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
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(LargeCategoryEnum.EXPENSES.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            WMText(text = expense.icon, style = typography.titleMedium)
        }

        Spacer(modifier = Modifier.width(8.dp))

        WMText(
            text = expense.name,
            style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )

        WMText(
            text = "${expense.amount.formatWithCommas()}원",
            style = typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = ColorGray.Gray_700
            )
        )
    }
}
