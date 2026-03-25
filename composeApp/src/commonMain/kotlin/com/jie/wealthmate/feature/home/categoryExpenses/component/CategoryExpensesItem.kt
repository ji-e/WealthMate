package com.jie.wealthmate.feature.home.categoryExpenses.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.InfoText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas

@Composable
fun CategoryExpensesItem(
    history: HistoryWithDetails,
    onClickHistory: (String) -> Unit = {},
) {
    val categoryInfoText = remember(history) {
        val category = history.category
        val categoryPart = buildString {
            category?.middleLabel?.takeIf { it.isNotBlank() }?.let { append(it) }

            category?.tags?.find { it.id == history.history.categoryTagId }?.tagLabel
                ?.takeIf { it.isNotBlank() }
                ?.let { tag ->
                    if (isNotEmpty()) append(" > ")
                    append(tag)
                }
        }
        categoryPart
    }

    val infoText = remember(history) {
        val paymentMethod = history.paymentMethod
        val installment = history.installment

        listOfNotNull(
            categoryInfoText.takeIf { it.isNotBlank() },
            paymentMethod?.label?.takeIf { it.isNotBlank() },
            installment?.let { "할부 ${history.history.installment?.installmentTime}/${it.count}회차" }
        ).joinToString(" | ")
    }

    Row(
        modifier = Modifier
            .clickable { onClickHistory(history.history.id) }
            .fillMaxWidth()
            .padding(horizontal = Padding.BackgroundHorizontal, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(end = Padding.SpacerXXS)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(Padding.SpacerXXS)
        ) {
            val content = history.history.content.default()
                .ifEmpty {
                    history.category?.tags?.find { it.id == history.history.categoryTagId }?.tagLabel
                }
            WMText(
                text = content.default().ifEmpty { "내용 미입력" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (content.isNullOrBlank()) ColorGray.Gray_300 else ColorGray.Gray_700,
                maxLines = 1
            )
            if (infoText.isNotBlank()) {
                InfoText(
                    text = infoText,
                    maxLines = 2
                )
            }
        }

        WMText(
            text = "${history.history.amount.formatWithCommas()}원",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
