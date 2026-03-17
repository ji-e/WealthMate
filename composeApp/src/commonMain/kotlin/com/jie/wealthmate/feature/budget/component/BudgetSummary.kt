package com.jie.wealthmate.feature.budget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.formatWithCommas
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

@Immutable
data class BudgetSummaryVo(
    val largeCategory: LargeCategoryEnum,
    val icon: String,
    val budgetAmount: Long,
    val currentAmount: Long,
)

@Composable
fun BudgetSummary(
    modifier: Modifier = Modifier,
    onDetailClick: () -> Unit = {},
    items: ImmutableList<BudgetSummaryVo>,
) {
    Column(modifier = modifier) {
        Row(
            modifier
                .noRippleClickable(onClick = { onDetailClick() })
                .padding(start = 28.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "요약",
                style = MaterialTheme.typography.titleSmall.copy(color = ColorGray.Gray_500),
            )

            Icon(
                painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                contentDescription = null,
                tint = ColorGray.Gray_500,
                modifier = Modifier.padding(start = 2.dp).size(16.dp)
            )
        }

        items.forEach { item ->
            StatusItem(
                item = item,
            )
        }
    }
}

@Composable
private fun StatusItem(
    item: BudgetSummaryVo,
    modifier: Modifier = Modifier,
) {
    // 상태 연산 최적화
    val barRatio by remember(item.budgetAmount, item.currentAmount) {
        derivedStateOf {
            if (item.budgetAmount > 0) (item.currentAmount.toFloat() / item.budgetAmount).coerceIn(
                0f,
                1f
            ) else 0f
        }
    }

    val percentage by remember(item.budgetAmount, item.currentAmount) {
        derivedStateOf {
            if (item.budgetAmount > 0) (item.currentAmount.toDouble() / item.budgetAmount * 100).toInt() else 0
        }
    }

    // 80% 초과 여부에 따른 색상 결정
    val statusColor by remember(item.largeCategory, percentage) {
        derivedStateOf {
            if (percentage > 80) {
                when (item.largeCategory) {
                    LargeCategoryEnum.INCOME -> ColorBlue.Blue_300
                    LargeCategoryEnum.EXPENSES -> ColorRed.Red_300
                    LargeCategoryEnum.SAVING -> ColorPrimary.Primary_500
                }
            } else {
                item.largeCategory.backgroundColor
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 카테고리 아이콘
        Box(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(item.largeCategory.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            WMText(text = item.icon, style = typography.titleLarge)
        }

        // 정보 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WMText(
                    text = item.largeCategory.label,
                    style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                WMText(
                    text = "$percentage%",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                    modifier = Modifier.padding(start = 4.dp)
                )

                WMText(
                    text = "${item.currentAmount.formatWithCommas()}원",
                    style = typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // 프로그레스 바
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorGray.Gray_200)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(barRatio)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor)
                    )
                }

                WMText(
                    text = "/ ${item.budgetAmount.formatWithCommas()}원",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
