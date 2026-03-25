package com.jie.wealthmate.feature.budget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right
import wealthmate.composeapp.generated.resources.ic_push_pin

@Immutable
data class BudgetOverUsageVo(
    val category: CategoryVo,
    val spentAmount: Long,
    val budgetAmount: Long,
    val overAmount: Long,
    val transactionCount: Int,
    val topExpenseTitle: String? = null,
    val topExpenseAmount: Long? = null,
    val topExpenseId: String? = null,
)

@Composable
fun BudgetOverPager(
    modifier: Modifier = Modifier,
    items: ImmutableList<BudgetOverUsageVo> = persistentListOf(),
    onTransactionCountClick: (largeCategory: String, categoryId: String) -> Unit = { _, _ -> },
) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        WMText(
            text = "예산 초과 카테고리 ${items.size}개",
            style = typography.titleSmall.copy(color = ColorGray.Gray_500),
            modifier = Modifier.padding(start = 28.dp, bottom = 12.dp),
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 28.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Box {
                BudgetOverItem(
                    modifier = Modifier.fillMaxWidth(),
                    totalSize = items.size,
                    index = page + 1,
                    item = items[page],
                    onTransactionCountClick = onTransactionCountClick
                )
            }
        }
    }
}


@Composable
fun BudgetOverItem(
    totalSize: Int,
    index: Int,
    item: BudgetOverUsageVo,
    modifier: Modifier = Modifier,
    onTransactionCountClick: (largeCategory: String, categoryId: String) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ColorRed.Red_50)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (totalSize > 1) {
                    WMText(
                        text = "$index / $totalSize",
                        style = typography.labelSmall.copy(
                            fontSize = 8.sp,
                            color = ColorGray.Gray_500
                        ),
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.6f))
                            .padding(horizontal = 4.dp)
                    )
                }
                WMText(
                    text = item.category.middleLabel,
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                )
            }

            CategoryIconWithPin(
                icon = item.category.icon,
                backgroundColor = item.category.largeCategory.backgroundColor,
                isFixed = item.category.isFixed
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        WMText(
            text = "지출 금액",
            style = typography.bodySmall.copy(color = ColorGray.Gray_500)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            WMText(
                text = "${item.spentAmount.formatWithCommas()}원",
                style = typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1
            )
            WMText(
                text = " / ${item.budgetAmount.formatWithCommas()}원",
                style = typography.bodySmall.copy(color = ColorGray.Gray_400),
                modifier = Modifier.padding(bottom = 2.dp, start = 4.dp),
                maxLines = 1
            )
        }

        WMText(
            text = "가장 많이 쓴 내역: ${item.topExpenseTitle?.ifEmpty { "미입력" }} (${item.topExpenseAmount?.formatWithCommas()}원)",
            style = typography.bodySmall.copy(color = ColorGray.Gray_500),
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoCard(
                modifier = Modifier.weight(1f),
                label = "초과 금액",
                value = "${item.overAmount.formatWithCommas()}원",
                valueColor = ColorRed.Red_300,
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                label = "거래 건수",
                value = "${item.transactionCount}건",
                onClick = {
                    onTransactionCountClick(item.category.largeCategory.name, item.category.id)
                }
            )
        }
    }
}

@Composable
private fun CategoryIconWithPin(
    icon: String,
    backgroundColor: Color,
    isFixed: Boolean,
) {
    Box(modifier = Modifier.size(48.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            WMText(text = icon, style = typography.titleLarge)
        }
        if (isFixed) {
            Icon(
                painter = painterResource(Res.drawable.ic_push_pin),
                contentDescription = null,
                tint = ColorRed.Red_300,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopStart)
            )
        }
    }
}

@Composable
private fun InfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = ColorGray.Gray_700,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .clickable(onClick != null) { onClick?.invoke() }
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WMText(
                text = label,
                style = typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (valueColor == ColorRed.Red_300) ColorRed.Red_300 else ColorGray.Gray_500
                )
            )
            if (onClick != null) {
                Icon(
                    painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                    contentDescription = "카테고리 이동",
                    tint = ColorSetting.Info,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        WMText(
            text = value,
            maxLines = 1,
            style = typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            ),
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = 14.sp,
                stepSize = 1.sp
            )
        )
    }
}
