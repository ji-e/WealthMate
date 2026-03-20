package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.EmojiIconSize
import com.jie.wealthmate.component.EmptyBoxView
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.component.vo.CategorySegmentedChartVo
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorChart
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right
import kotlin.math.roundToInt

@Composable
fun CategorySegmentedChart(
    statusType: StatusType,
    expensesAmount: Long,
    categorySegmentChartItems: List<CategorySegmentedChartVo>,
    modifier: Modifier = Modifier,
    onCategoryChartClick: () -> Unit = {},
) {
    val typography = MaterialTheme.typography
    var isStarted by remember { mutableStateOf(false) }

    // UI State에서 가공된 데이터를 그대로 사용하며, 색상 및 총액 기준 설정
    val colors = remember { ColorChart.getCategoryChartColors() }
    val totalAmount = expensesAmount.coerceAtLeast(1L)

    val animProgress by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "ChartAnimation"
    )

    LaunchedEffect(Unit) { isStarted = true }

    Column(modifier = modifier.fillMaxWidth()) {
        // 헤더 영역
        Row(
            modifier = Modifier
                .padding(horizontal = Padding.BackgroundHorizontal)
                .noRippleClickable(onClick = onCategoryChartClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "${statusType.label} 카테고리별 지출",
                style = typography.titleSmall,
                color = ColorSetting.Info
            )

            Icon(
                painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                contentDescription = null,
                tint = ColorSetting.Info,
                modifier = Modifier.size(16.dp)
            )
        }

        WMSpacer(size = SpacerSize.SMALL)

        if (categorySegmentChartItems.isEmpty()) {
            EmptyBoxView(
                modifier = Modifier.padding(horizontal = Padding.BackgroundHorizontal),
                contentText = "지출 내역이 없습니다.",
            )
        } else {
            // 차트 바 영역
            Row(
                modifier = Modifier
                    .padding(horizontal = Padding.BackgroundHorizontal)
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(Shapes.medium)
            ) {
                categorySegmentChartItems.forEachIndexed { index, data ->
                    val proportion = data.amount.toFloat() / totalAmount.toFloat()
                    val currentWeight = (proportion * animProgress).coerceAtLeast(0.0001f)
                    val color = colors[index % colors.size]

                    Box(
                        modifier = Modifier
                            .weight(currentWeight)
                            .fillMaxHeight()
                            .background(color)
                    )
                }

                // 애니메이션 중 남은 공간 처리
                if (animProgress < 1f) {
                    val remainingWeight = (1f - animProgress).coerceAtLeast(0.0001f)
                    Box(modifier = Modifier.weight(remainingWeight).fillMaxHeight())
                }
            }

            WMSpacer(size = SpacerSize.X_SMALL)

            // 리스트 아이템 영역
            categorySegmentChartItems.forEachIndexed { index, item ->
                val color = colors[index % colors.size]
                SegmentedItem(
                    icon = item.category.icon,
                    label = item.category.middleLabel,
                    color = color,
                    totalAmount = totalAmount,
                    amount = item.amount,
                )
            }
        }
    }
}

@Composable
fun SegmentedItem(
    icon: String?,
    label: String,
    color: Color,
    totalAmount: Long,
    amount: Long,
) {
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = Padding.BackgroundHorizontal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EmojiIcon(
            icon = icon,
            color = color,
            size = EmojiIconSize.SMALL
        )

        Row(
            modifier = Modifier
                .padding(start = Padding.SpacerXS)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val rate = remember(totalAmount, amount) {
                    if (totalAmount <= 0L) 0
                    else (amount.toDouble() / totalAmount.toDouble() * 100.0).roundToInt()
                }

                WMText(
                    text = label,
                    modifier = Modifier.weight(1f, fill = false),
                    style = typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )

                WMText(
                    text = "${rate}%",
                    style = typography.bodySmall,
                    color = ColorSetting.Info,
                    modifier = Modifier.padding(start = Padding.SpacerXXS),
                    maxLines = 1
                )
            }

            WMText(
                text = "${amount.formatWithCommas()}원",
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun CategorySegmentedChartPreview() {
    val mockItems = listOf(
        CategorySegmentedChartVo(
            category = CategoryVo(
                id = "1",
                icon = "🍔",
                largeCategory = LargeCategoryEnum.EXPENSES,
                middleLabel = "식비",
                sort = 1,
                isFixed = false,
                tags = persistentListOf()
            ),
            amount = 500000
        ),
        CategorySegmentedChartVo(
            category = CategoryVo(
                id = "2",
                icon = "🏠",
                largeCategory = LargeCategoryEnum.EXPENSES,
                middleLabel = "주거",
                sort = 2,
                isFixed = true,
                tags = persistentListOf()
            ),
            amount = 300000
        ),
        CategorySegmentedChartVo(
            category = CategoryVo(
                id = "3",
                icon = "🚗",
                largeCategory = LargeCategoryEnum.EXPENSES,
                middleLabel = "교통교통교통교통교통교통교통교통",
                sort = 3,
                isFixed = false,
                tags = persistentListOf()
            ),
            amount = 150000
        ),
        CategorySegmentedChartVo(
            category = CategoryVo(
                id = "others",
                icon = "•••",
                largeCategory = LargeCategoryEnum.EXPENSES,
                middleLabel = "그 외 3개",
                sort = 0,
                isFixed = false,
                tags = persistentListOf()
            ),
            amount = 50000
        )
    )

    WMTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 16.dp)
        ) {
            CategorySegmentedChart(
                statusType = StatusType.MONTH,
                expensesAmount = 1000000,
                categorySegmentChartItems = mockItems,
            )
        }
    }
}

@Preview
@Composable
private fun CategorySegmentedChartEmptyPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 16.dp)
        ) {
            CategorySegmentedChart(
                statusType = StatusType.MONTH,
                expensesAmount = 0,
                categorySegmentChartItems = emptyList(),
            )
        }
    }
}
