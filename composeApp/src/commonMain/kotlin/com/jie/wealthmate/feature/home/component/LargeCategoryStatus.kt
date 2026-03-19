package com.jie.wealthmate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.Amount
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun LargeCategoryStatus(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    currentAmount: Amount?,
    lastAmount: Amount?,
    onCategoryClick: (LargeCategoryEnum) -> Unit = {},
) {
    val typography = MaterialTheme.typography

    // 수입 대비 비율 미리 계산
    val expensesRate = remember(currentAmount) {
        calculateRate(
            currentAmount?.incomeAmount.default(),
            currentAmount?.expensesAmount.default()
        )
    }
    val savingRate = remember(currentAmount) {
        calculateRate(currentAmount?.incomeAmount.default(), currentAmount?.savingAmount.default())
    }

    Column(modifier = modifier.fillMaxWidth()) {
        WMText(
            text = "${statusType.lastLabel} 대비 현황",
            style = typography.titleSmall.copy(color = ColorGray.Gray_500),
            modifier = Modifier.padding(start = 28.dp, bottom = 8.dp)
        )

        StatusItem(
            largeCategory = LargeCategoryEnum.INCOME,
            icon = "💰",
            statusType = statusType,
            currentAmount = currentAmount?.incomeAmount.default(),
            lastAmount = lastAmount?.incomeAmount.default(),
            onClick = { onCategoryClick(LargeCategoryEnum.INCOME) }
        )

        StatusItem(
            largeCategory = LargeCategoryEnum.EXPENSES,
            icon = "💸",
            statusType = statusType,
            currentAmount = currentAmount?.expensesAmount.default(),
            lastAmount = lastAmount?.expensesAmount.default(),
            comparedToIncomePercent = expensesRate,
            onClick = { onCategoryClick(LargeCategoryEnum.EXPENSES) }
        )

        StatusItem(
            largeCategory = LargeCategoryEnum.SAVING,
            icon = "🏦",
            statusType = statusType,
            currentAmount = currentAmount?.savingAmount.default(),
            lastAmount = lastAmount?.savingAmount.default(),
            comparedToIncomePercent = savingRate,
            onClick = { onCategoryClick(LargeCategoryEnum.SAVING) }
        )
    }
}

@Composable
private fun StatusItem(
    largeCategory: LargeCategoryEnum,
    icon: String,
    statusType: StatusType,
    currentAmount: Long,
    lastAmount: Long,
    comparedToIncomePercent: Int? = null,
    onClick: () -> Unit,
) {
    val typography = MaterialTheme.typography
    val changeMessage = remember(currentAmount, lastAmount, statusType) {
        getChangeMessage(currentAmount, lastAmount, statusType)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(start = 28.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(end = 10.dp)
                .clip(CircleShape)
                .background(largeCategory.backgroundColor)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = icon,
                style = typography.titleLarge,
            )
        }

        Column(modifier = Modifier.padding(end = 8.dp)) {
            WMText(
                text = largeCategory.label,
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            WMText(
                text = changeMessage,
                style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            WMText(
                text = "${formatWithCommas(currentAmount.toString())}원",
                style = typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                textAlign = TextAlign.End,
            )

            comparedToIncomePercent?.let {
                WMText(
                    text = "수입 대비 ${it}%",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                )
            }
        }

        Icon(
            painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
            contentDescription = null,
            tint = ColorGray.Gray_500,
            modifier = Modifier.size(24.dp).padding(start = 4.dp)
        )
    }
}

private fun calculateContrastPercentage(
    current: Long,
    last: Long,
): Int? {
    if (last == 0L) return null
    return (((current - last).toFloat() / last) * 100f).roundToInt()
}

fun calculateRate(
    total: Long,
    target: Long,
): Int? {
    if (total == 0L) return null
    return ((target.toFloat() / total) * 100f).roundToInt()
}

private fun getChangeMessage(
    current: Long,
    last: Long,
    statusType: StatusType
): String {
    val percentage = calculateContrastPercentage(current, last) ?: return "데이터가 없어요"

    return when {
        percentage > 0 -> "+${percentage}%"
        percentage < 0 -> "-${percentage.absoluteValue}%"
        else -> "${statusType.lastLabel}과 동일해요"
    }
}
