package com.jie.wealthmate.feature.home.preparednessStatus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.budgetYearDetail.component.LegendItem
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorGroup
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.PaymentMethodDiffInfoVo
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun StatusPaymentMethodSection(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    largeCategory: LargeCategoryEnum,
    comparisons: List<PaymentMethodDiffInfoVo>,
    onPaymentMethodClick: (String?) -> Unit = {},
) {
    val typography = MaterialTheme.typography
    val maxAmount = remember(comparisons) {
        comparisons.maxOfOrNull { max(it.currentAmount, it.lastAmount) }?.coerceAtLeast(1L) ?: 1L
    }

    val colorList = ColorGroup.getColorList()
    val groupColorMap = remember(comparisons) {
        comparisons
            .filter { it.groupLabel != null }
            .distinctBy { it.groupLabel }
            .mapIndexed { index, item ->
                item.groupLabel to colorList[index % colorList.size].second
            }
            .toMap()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "결제수단별 비교",
                style = typography.titleSmall.copy(
                    color = ColorGray.Gray_500,
                    fontWeight = FontWeight.SemiBold
                ),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendItem(label = statusType.lastLabel, color = ColorGray.Gray_200)
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(label = statusType.label, color = largeCategory.accentColor)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            comparisons.forEach { info ->
                StatusPaymentMethodItem(
                    largeCategory = largeCategory,
                    info = info,
                    maxAmount = maxAmount,
                    groupBackgroundColor = groupColorMap[info.groupLabel] ?: ColorGray.Gray_200,
                    onClick = { onPaymentMethodClick(info.id) }
                )
            }
        }
    }
}

@Composable
private fun StatusPaymentMethodItem(
    largeCategory: LargeCategoryEnum,
    info: PaymentMethodDiffInfoVo,
    maxAmount: Long,
    groupBackgroundColor: Color,
    onClick: () -> Unit,
) {
    val typography = MaterialTheme.typography
    val percentage = remember(info.currentAmount, info.lastAmount) {
        if (info.lastAmount == 0L) {
            if (info.currentAmount > 0) 100 else 0
        } else {
            ((info.diffAmount.toFloat() / info.lastAmount) * 100f).roundToInt()
        }
    }

    Column(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (info.groupLabel.isNullOrBlank().not()) {
                    WMText(
                        text = info.groupLabel!!,
                        style = typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(groupBackgroundColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    WMText(
                        text = info.label,
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                    )

                    WMText(
                        text = if (percentage >= 0) "+$percentage%" else "$percentage%",
                        style = typography.labelSmall.copy(color = ColorGray.Gray_500),
                        maxLines = 1,
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                WMText(
                    text = "${info.currentAmount.formatWithCommas()}원",
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
                val diffColor = when {
                    info.diffAmount > 0 -> ColorRed.Red_300
                    info.diffAmount < 0 -> ColorBlue.Blue_300
                    else -> ColorGray.Gray_400
                }
                val sign = if (info.diffAmount > 0) "+" else ""
                WMText(
                    text = "$sign${info.diffAmount.formatWithCommas()}원",
                    style = typography.labelSmall.copy(color = diffColor),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusComparisonProgressBar(
                amount = info.lastAmount,
                maxAmount = maxAmount,
                color = ColorGray.Gray_200
            )
            StatusComparisonProgressBar(
                amount = info.currentAmount,
                maxAmount = maxAmount,
                color = largeCategory.accentColor
            )
        }
    }
}

@Composable
private fun StatusComparisonProgressBar(
    amount: Long,
    maxAmount: Long,
    color: Color,
) {
    val fraction = (amount.toFloat() / maxAmount).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(ColorGray.Gray_50)
    ) {
        if (amount > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}
