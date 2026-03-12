package com.jie.wealthmate.feature.budget.budgetDetail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.addBudget.component.CategoryIcon
import com.jie.wealthmate.feature.budget.budgetDetail.BudgetSectionVo
import com.jie.wealthmate.feature.budget.budgetDetail.CategoryBudgetGroupVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.formatWithCommas
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down

@Composable
fun SectionHeader(
    section: BudgetSectionVo,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.noRippleClickable { onToggle() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WMText(
                    text = section.largeCategory.label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ColorPrimary.Primary_500,
                        fontWeight = FontWeight.SemiBold
                    ),
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = section.largeCategory.label,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation),
                    tint = ColorPrimary.Primary_500,
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            val budgetText = remember(section.totalUsed, section.totalBudget) {
                val used = section.totalUsed.formatWithCommas()
                val budget =
                    if (section.totalBudget == 0L) "예산 미설정"
                    else "${section.totalBudget.formatWithCommas()}원"

                "총 ${used}원 / $budget"
            }

            WMText(
                text = budgetText,
                style = MaterialTheme.typography.bodySmall.copy(color = ColorGray.Gray_500)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = ColorGray.Gray_100
        )
    }
}

@Composable
fun CategoryBudgetGroup(
    group: CategoryBudgetGroupVo,
    largeCategory: LargeCategoryEnum,
    modifier: Modifier = Modifier,
) {
    var isTagsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    ) {
        // 상위 카테고리 정보
        CategoryBudgetItem(
            icon = group.category.icon,
            isFixed = group.category.isFixed,
            title = group.category.middleLabel,
            usedAmount = group.totalUsed,
            budgetAmount = group.totalBudget,
            percentage = group.percentage,
            largeCategory = largeCategory,
            isMain = true,
            hasTags = group.tagBudgets.isNotEmpty(),
            isTagsExpanded = isTagsExpanded,
            onToggleTags = { isTagsExpanded = !isTagsExpanded }
        )

        // 태그(상세) 정보가 있는 경우
        AnimatedVisibility(
            visible = isTagsExpanded && group.tagBudgets.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ColorGray.Gray_50)
                        .padding(16.dp)
                ) {
                    WMText(
                        text = "${group.category.middleLabel} 상세 태그",
                        style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    group.tagBudgets.forEachIndexed { index, tagBudget ->
                        CategoryBudgetItem(
                            icon = null,
                            isFixed = group.category.isFixed,
                            title = tagBudget.tag?.label ?: "",
                            usedAmount = tagBudget.usedAmount,
                            budgetAmount = tagBudget.budgetAmount,
                            percentage = tagBudget.percentage,
                            largeCategory = largeCategory,
                            isMain = false
                        )
                        if (index < group.tagBudgets.lastIndex) {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryBudgetItem(
    icon: String?,
    isFixed: Boolean,
    title: String,
    usedAmount: Long,
    budgetAmount: Long,
    percentage: Int,
    largeCategory: LargeCategoryEnum,
    isMain: Boolean,
    hasTags: Boolean = false,
    isTagsExpanded: Boolean = false,
    onToggleTags: () -> Unit = {},
) {
    val barRatio = remember(budgetAmount, usedAmount) {
        if (budgetAmount > 0) (usedAmount.toFloat() / budgetAmount).coerceIn(0f, 1f)
        else if (usedAmount > 0) 1f
        else 0f
    }

    val rotation by animateFloatAsState(targetValue = if (isTagsExpanded) 180f else 0f)

    val statusColor = remember(largeCategory, percentage, isMain, budgetAmount, usedAmount) {
        when {
            budgetAmount == 0L && usedAmount > 0 -> ColorGray.Gray_300
            percentage > 80 -> when (largeCategory) {
                LargeCategoryEnum.INCOME -> ColorBlue.Blue_300
                LargeCategoryEnum.EXPENSES -> ColorRed.Red_300
                LargeCategoryEnum.SAVING -> ColorPrimary.Primary_500
            }

            isMain -> largeCategory.backgroundColor
            else -> ColorPrimary.Primary_300
        }
    }

    Column(
        modifier = Modifier
            .padding(start = if (isMain) 20.dp else 0.dp, end = if (isMain) 28.dp else 0.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isMain) {
                CategoryIcon(
                    icon = icon ?: "❓",
                    backgroundColor = largeCategory.backgroundColor,
                    isFixed = isFixed,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = if (isMain && hasTags) Modifier.noRippleClickable { onToggleTags() } else Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WMText(
                        text = title,
                        style =
                            if (isMain) typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            else typography.titleSmall.copy(fontWeight = FontWeight.Medium)
                    )

                    if (isMain && hasTags) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_drop_down),
                            contentDescription = "상세 태그 보기",
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotation),
                        )
                    }
                }

                WMText(
                    text = if (budgetAmount == 0L) "-" else "$percentage%",
                    style = typography.labelMedium.copy(color = ColorGray.Gray_500)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                WMText(
                    text = "${usedAmount.formatWithCommas()}원",
                    style =
                        if (isMain) typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        else typography.titleSmall.copy(fontWeight = FontWeight.Medium)
                )
                WMText(
                    text =
                        if (budgetAmount == 0L) "/ 예산 미설정"
                        else "/ ${budgetAmount.formatWithCommas()}원",
                    style = typography.labelMedium.copy(color = ColorGray.Gray_400),
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .padding(start = if (isMain) 8.dp else 0.dp)
                .fillMaxWidth()
                .height(if (isMain) 10.dp else 8.dp)
                .clip(CircleShape)
                .background(ColorGray.Gray_200)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(barRatio)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(statusColor)
            )
        }
    }
}
