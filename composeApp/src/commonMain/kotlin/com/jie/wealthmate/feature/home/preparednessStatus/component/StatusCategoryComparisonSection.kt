package com.jie.wealthmate.feature.home.preparednessStatus.component

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
import androidx.compose.material3.Icon
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
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.CategoryDiffInfoVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right
import wealthmate.composeapp.generated.resources.ic_push_pin
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * 카테고리별 지난 기간과 현재 기간의 금액 비교 섹션 (막대 그래프)
 */
@Composable
fun StatusCategoryComparisonSection(
    modifier: Modifier = Modifier,
    title: String,
    statusType: StatusType,
    largeCategory: LargeCategoryEnum,
    comparisons: List<CategoryDiffInfoVo>,
    onCategoryClick: (String) -> Unit,
) {
    val typography = MaterialTheme.typography
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
                LegendItem(label = statusType.label, color = largeCategory.accentColor)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            comparisons.forEach { info ->
                StatusCategoryComparisonItem(
                    largeCategory = largeCategory,
                    info = info,
                    maxAmount = maxAmount,
                    iconBackgroundColor = largeCategory.backgroundColor,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}


/**
 * 고정 카테고리별 지난 기간과 현재 기간의 금액 비교 섹션 (막대 그래프)
 */
@Composable
fun StatusFixedCategoryComparisonSection(
    modifier: Modifier = Modifier,
    title: String,
    statusType: StatusType,
    largeCategory: LargeCategoryEnum,
    comparisons: List<CategoryDiffInfoVo>,
    onCategoryClick: (String) -> Unit,
) {
    val typography = MaterialTheme.typography
    val maxAmount = remember(comparisons) {
        comparisons.maxOfOrNull { max(it.currentAmount, it.lastAmount) }?.coerceAtLeast(1L) ?: 1L
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.White)
            .padding(vertical = 12.dp)
            .padding(end = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 20.dp)
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
                LegendItem(label = statusType.label, color = largeCategory.accentColor)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            comparisons.forEach { info ->
                StatusCategoryComparisonItem(
                    largeCategory = largeCategory,
                    info = info,
                    isFixed = true,
                    maxAmount = maxAmount,
                    iconBackgroundColor = largeCategory.backgroundColor,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}

@Composable
private fun StatusCategoryComparisonItem(
    largeCategory: LargeCategoryEnum,
    info: CategoryDiffInfoVo,
    isFixed: Boolean = false,
    maxAmount: Long,
    iconBackgroundColor: Color,
    onCategoryClick: (String) -> Unit,
) {
    val typography = MaterialTheme.typography
    val percentage = remember(info.currentAmount, info.lastAmount) {
        if (info.lastAmount == 0L) {
            if (info.currentAmount > 0) 100 else 0
        } else {
            ((info.diffAmount.toFloat() / info.lastAmount) * 100f).roundToInt()
        }
    }

    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { onCategoryClick(info.categoryId.default()) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusCategoryIcon(
                    icon = info.categoryIcon,
                    backgroundColor = iconBackgroundColor,
                    isFixed = isFixed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WMText(
                            text = info.categoryName,
                            modifier = Modifier.weight(1f, fill = false),
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            maxLines = 1,
                        )
                        Icon(
                            painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                            contentDescription = null,
                            tint = ColorGray.Gray_500,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .size(16.dp)
                        )
                    }

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
            modifier = Modifier.padding(start = if (isFixed) 16.dp else 0.dp),
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
fun StatusCategoryIcon(icon: String, backgroundColor: Color, isFixed: Boolean = false) {
    Box(
        modifier = Modifier.width(if (isFixed) 56.dp else 40.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center
        ) {
            WMText(text = icon, style = MaterialTheme.typography.titleLarge)
        }

        if (isFixed) {
            Icon(
                painter = painterResource(Res.drawable.ic_push_pin),
                contentDescription = null,
                tint = ColorRed.Red_300,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
                    .align(Alignment.TopStart)
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
