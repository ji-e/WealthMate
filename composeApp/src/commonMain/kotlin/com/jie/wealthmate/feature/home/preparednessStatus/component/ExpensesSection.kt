package com.jie.wealthmate.feature.home.preparednessStatus.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
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
import com.jie.wealthmate.feature.home.preparednessStatus.CategoryDiffInfo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.formatWithCommas
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ExpensesSection(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    maxIncreaseCategory: CategoryDiffInfo?,
    maxDecreaseCategory: CategoryDiffInfo?,
    categoryComparisons: List<CategoryDiffInfo> = emptyList(),
    fixedCategoryComparisons: List<CategoryDiffInfo> = emptyList(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (maxIncreaseCategory != null || maxDecreaseCategory != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                maxIncreaseCategory?.let {
                    CategoryDiffItem(
                        label = "변동지출 최대 증가 📈",
                        info = it,
                        diffColor = ColorRed.Red_300
                    )
                }
                maxDecreaseCategory?.let {
                    CategoryDiffItem(
                        label = "변동지출 최대 감소 📉",
                        info = it,
                        diffColor = ColorBlue.Blue_300
                    )
                }
            }
        }

        if (categoryComparisons.isNotEmpty()) {
            CategoryComparisonSection(
                modifier = Modifier.padding(top = 24.dp),
                title = "변동지출 카테고리별 비교",
                statusType = statusType,
                comparisons = categoryComparisons
            )
        }

        if (fixedCategoryComparisons.isNotEmpty()) {
            CategoryComparisonSection(
                modifier = Modifier.padding(top = 24.dp),
                title = "고정지출 카테고리별 비교",
                statusType = statusType,
                comparisons = fixedCategoryComparisons
            )
        }
    }
}

@Composable
private fun CategoryComparisonSection(
    modifier: Modifier = Modifier,
    title: String,
    statusType: StatusType,
    comparisons: List<CategoryDiffInfo>,
) {
    val maxAmount = remember(comparisons) {
        comparisons.maxOfOrNull { max(it.currentAmount, it.lastAmount) }?.coerceAtLeast(1L) ?: 1L
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
                text = title,
                style = typography.titleSmall.copy(
                    color = ColorGray.Gray_500,
                    fontWeight = FontWeight.SemiBold
                ),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendItem(label = statusType.lastLabel, color = ColorGray.Gray_200)
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(label = statusType.label, color = ColorPrimary.Primary_500)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            comparisons.forEach { info ->
                CategoryComparisonItem(
                    info = info,
                    maxAmount = maxAmount
                )
            }
        }
    }
}

@Composable
private fun CategoryComparisonItem(
    info: CategoryDiffInfo,
    maxAmount: Long
) {
    val percentage = remember(info.currentAmount, info.lastAmount) {
        when {
            info.lastAmount == 0L && info.currentAmount > 0 -> 100
            info.lastAmount == 0L -> 0
            else -> ((info.diffAmount.toFloat() / info.lastAmount) * 100f).roundToInt()
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIcon(icon = info.categoryIcon)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    WMText(
                        text = info.categoryName,
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                    )
                    WMText(
                        text = if (percentage >= 0) "+$percentage%" else "$percentage%",
                        style = typography.labelSmall.copy(color = ColorGray.Gray_500)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                WMText(
                    text = "${formatWithCommas(info.currentAmount.toString())}원",
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                val diffColor = when {
                    info.diffAmount > 0 -> ColorRed.Red_300
                    info.diffAmount < 0 -> ColorBlue.Blue_300
                    else -> ColorGray.Gray_400
                }
                val sign = if (info.diffAmount > 0) "+" else ""
                WMText(
                    text = "$sign${formatWithCommas(info.diffAmount.toString())}원",
                    style = typography.labelSmall.copy(color = diffColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ComparisonProgressBar(
                amount = info.lastAmount,
                maxAmount = maxAmount,
                color = ColorGray.Gray_200
            )
            ComparisonProgressBar(
                amount = info.currentAmount,
                maxAmount = maxAmount,
                color = ColorPrimary.Primary_500
            )
        }
    }
}

@Composable
private fun CategoryIcon(icon: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(LargeCategoryEnum.EXPENSES.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        WMText(text = icon, style = typography.titleLarge)
    }
}

@Composable
private fun ComparisonProgressBar(
    amount: Long,
    maxAmount: Long,
    color: Color
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
        } else if (amount == 0L) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ColorGray.Gray_100)
            )
        }
    }
}

@Composable
private fun RowScope.CategoryDiffItem(
    label: String,
    info: CategoryDiffInfo,
    diffColor: Color,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        WMText(
            text = label,
            style = typography.titleSmall.copy(
                color = ColorGray.Gray_500,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryIcon(icon = info.categoryIcon)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                WMText(
                    text = info.categoryName,
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                val sign = if (info.diffAmount > 0) "+" else ""
                WMText(
                    text = "$sign${formatWithCommas(info.diffAmount.toString())}원",
                    style = typography.labelMedium.copy(color = diffColor)
                )
            }
        }
    }
}
