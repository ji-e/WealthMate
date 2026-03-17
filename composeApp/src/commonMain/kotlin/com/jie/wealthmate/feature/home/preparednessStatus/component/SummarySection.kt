package com.jie.wealthmate.feature.home.preparednessStatus.component

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
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import kotlin.math.absoluteValue

@Composable
fun SummarySection(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    largeCategory: LargeCategoryEnum,
    diffPercentage: Int?,
    diffAmount: Long,
    currentAmount: Long,
) {
    val isExpenses = largeCategory == LargeCategoryEnum.EXPENSES

    val textColor = remember(diffPercentage, isExpenses) {
        when {
            diffPercentage == null || diffPercentage == 0 -> ColorGray.Gray_400
            isExpenses -> if (diffPercentage > 0) ColorRed.Red_300 else ColorBlue.Blue_300
            else -> if (diffPercentage > 0) ColorBlue.Blue_300 else ColorRed.Red_300
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WMText(
                text = "${statusType.label} 총 ${largeCategory.label}",
                style = MaterialTheme.typography.titleSmall.copy(color = ColorGray.Gray_500)
            )

            diffPercentage?.let {
                val sign = if (it > 0) "+" else ""
                val typeText = when (statusType) {
                    StatusType.WEEK -> "지난주와"
                    StatusType.MONTH -> "지난달과"
                    StatusType.YEAR -> "지난해와"
                }
                val displayText = if (it == 0) "$typeText 동일" else "$sign$it%"

                WMText(
                    text = displayText,
                    style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        WMText(
            text = "${formatWithCommas(currentAmount.toString())}원",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        val diffActionText = when (largeCategory) {
            LargeCategoryEnum.INCOME -> if (diffAmount >= 0) "늘었어요" else "줄었어요"
            LargeCategoryEnum.EXPENSES -> if (diffAmount >= 0) "더 썼어요" else "덜 썼어요"
            LargeCategoryEnum.SAVING -> if (diffAmount >= 0) "더 했어요" else "덜 했어요"
        }

        Row(verticalAlignment = Alignment.CenterVertically) {


            WMText(
                text = "${statusType.lastLabel} 대비 ",
                style = MaterialTheme.typography.bodyMedium.copy(color = ColorGray.Gray_500)
            )
            WMText(
                text = "${formatWithCommas(diffAmount.absoluteValue.toString())}원 ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            )
            WMText(
                text = diffActionText,
                style = MaterialTheme.typography.bodyMedium.copy(color = ColorGray.Gray_500)
            )
        }
    }
}