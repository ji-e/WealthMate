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
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.budgetYearDetail.FixedExpense
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun ExpenseFixed(
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
            .padding(horizontal = Padding.ContainerHorizontal, vertical = Padding.ContainerVertical)
    ) {
        WMText(
            text = "고정지출",
            style = typography.titleSmall,
            color = ColorSetting.Info,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WMText(
                text = "총 ${totalFixedAmount.formatWithCommas()}원",
                fontWeight = FontWeight.SemiBold
            )

            if (totalExpense > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                WMText(
                    text = "전체 지출의 ${percentage}%",
                    style = typography.labelSmall,
                    color = ColorSetting.Info
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
                    color = ColorSetting.EmptyContent
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
            .background(ColorSetting.EmptyBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        EmojiIcon(
            icon = expense.icon,
            color = LargeCategoryEnum.EXPENSES.backgroundColor
        )

        Spacer(modifier = Modifier.width(Padding.SpacerXS))

        WMText(
            text = expense.name,
            style = typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        WMText(
            text = "${expense.amount.formatWithCommas()}원",
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = ColorSetting.Default

        )
    }
}
