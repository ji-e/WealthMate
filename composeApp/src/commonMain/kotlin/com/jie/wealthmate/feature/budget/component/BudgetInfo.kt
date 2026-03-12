package com.jie.wealthmate.feature.budget.component

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.collections.immutable.ImmutableList

@Composable
fun BudgetInfo(
    modifier: Modifier = Modifier,
    isNotBudgetSetting: Boolean,
    topExpenses: ImmutableList<HistoryVo>,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ColorRed.Red_50)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isNotBudgetSetting) {
            WMText(
                text = "🖐🏻",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            WMText(
                text = "지출 예산을 설정해주세요.",
                style = MaterialTheme.typography.titleSmall
            )
        } else {
            WMText(
                text = "가장 많이 쓴 내역 Top3",
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topExpenses.forEach { expense ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LargeCategoryEnum.EXPENSES.backgroundColor)
                                .size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            WMText(
                                text = expense.category?.icon?:"❓",
                                style = typography.bodySmall,
                            )
                        }

                        WMText(
                            text = expense.content.default().ifEmpty { "미입력" },
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                        WMText(
                            text = "${formatWithCommas(expense.amount.toString())}원",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }
}
