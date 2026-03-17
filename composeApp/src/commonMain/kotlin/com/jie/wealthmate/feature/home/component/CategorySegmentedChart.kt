package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.theme.ColorChart
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right
import kotlin.math.roundToInt


@Composable
fun CategorySegmentedChart(
    statusType: StatusType,
    modifier: Modifier = Modifier,
    expensesAmount: Long,
    categorySegmentChartItems: List<CategorySegmentChartData>,
    onCategoryChartClick: () -> Unit = {},
) {
    val typography = MaterialTheme.typography
    var isStarted by remember { mutableStateOf(false) }

    // 1. 전체 데이터 구성 (미지정 항목 포함 및 금액 내림차순 정렬)
    val displayItems = remember(categorySegmentChartItems, expensesAmount) {
        val assignedSum = categorySegmentChartItems.sumOf { it.amount }
        // 전체 지출(expensesAmount)에서 분류된 카테고리 합을 뺀 나머지가 '카테고리 없음'
        val unassignedAmount = (expensesAmount - assignedSum).coerceAtLeast(0L)

        val allItems = if (unassignedAmount > 0) {
            categorySegmentChartItems + CategorySegmentChartData(
                category = CategoryVo.unset(), // '카테고리 없음' 객체 생성
                amount = unassignedAmount
            )
        } else {
            categorySegmentChartItems
        }

        allItems.sortedByDescending { it.amount }
    }

    // 2. 분모 설정 (전체 지출을 기준으로 백분율 계산)
    val totalAmountForCalc = remember(displayItems, expensesAmount) {
        displayItems.sumOf { it.amount }.coerceAtLeast(expensesAmount).toFloat()
    }

    val colors = remember { ColorChart.getCategoryChartColors() }

    val animProgress by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "ChartAnimation"
    )

    LaunchedEffect(Unit) { isStarted = true }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .noRippleClickable(onClick = onCategoryChartClick)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = "${statusType.label} 카테고리별 지출",
                style = typography.titleSmall.copy(color = ColorGray.Gray_500),
            )
            Icon(
                painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
                contentDescription = null,
                tint = ColorGray.Gray_500,
                modifier = Modifier.size(16.dp)
            )
        }

        if (displayItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(color = ColorGray.Gray_50, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = "카테고리가 없습니다.",
                    style = MaterialTheme.typography.bodySmall.copy(color = ColorGray.Gray_400)
                )
            }
            return
        }

        // 차트 바 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
        ) {
            if (totalAmountForCalc > 0) {
                displayItems.forEachIndexed { index, data ->
                    val proportion = data.amount / totalAmountForCalc
                    val currentWeight = proportion * animProgress

                    if (currentWeight > 0f) {
                        val color = if (data.category.isUnset) ColorGray.Gray_200
                        else colors[index % colors.size]

                        Box(
                            modifier = Modifier
                                .weight(currentWeight)
                                .fillMaxHeight()
                                .background(color)
                        )
                    }
                }

                if (animProgress < 1f) {
                    Box(
                        modifier = Modifier
                            .weight((1f - animProgress).coerceAtLeast(0.001f))
                            .fillMaxHeight()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 리스트 아이템 영역
        displayItems.forEachIndexed { index, item ->
            val color = if (item.category.isUnset) ColorGray.Gray_200
            else colors[index % colors.size]

            CategorySegmentedItem(
                totalAmount = totalAmountForCalc.toLong(),
                categorySegment = item,
                color = color
            )
        }
    }
}

@Composable
private fun CategorySegmentedItem(
    totalAmount: Long,
    categorySegment: CategorySegmentChartData,
    color: Color,
) {
    val typography = MaterialTheme.typography
    val category = categorySegment.category

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color)
                .size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = category.icon,
                style = typography.bodySmall,
            )
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                WMText(
                    text = category.middleLabel,
                    style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                )

                val rate = remember(totalAmount, categorySegment.amount) {
                    if (totalAmount == 0L) 0
                    else (categorySegment.amount.toDouble() / totalAmount.toDouble() * 100.0).roundToInt()
                }

                WMText(
                    text = "${rate}%",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            WMText(
                text = "${formatWithCommas(categorySegment.amount.toString())}원",
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1
            )
        }
    }
}

data class CategorySegmentChartData(
    val category: CategoryVo,
    val amount: Long,
)
