package com.jie.wealthmate.feature.home.preparednessStatus.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorChart
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.vo.CategoryDiffInfoVo
import kotlin.math.roundToInt

/**
 * 카테고리별 비중을 보여주는 도넛 차트 섹션
 */
@Composable
fun StatusDonutChartSection(
    title: String,
    totalLabel: String,
    categories: List<CategoryDiffInfoVo>,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val colors = remember { ColorChart.getCategoryChartColors() }

    // 최대 5개까지만 노출, 넘어가면 '그 외 n개'로 묶음
    val processedCategories = remember(categories) {
        if (categories.size <= 5) {
            categories
        } else {
            val topCategories = categories.take(4)
            val otherCategories = categories.drop(4)
            val otherAmount = otherCategories.sumOf { it.currentAmount }
            val otherDiff = otherCategories.sumOf { it.diffAmount }
            val otherRatio = otherCategories.sumOf { it.ratio.toDouble() }.toFloat()

            topCategories + CategoryDiffInfoVo(
                categoryId = null,
                categoryIcon = "···",
                categoryName = "그 외 ${otherCategories.size}개",
                currentAmount = otherAmount,
                diffAmount = otherDiff,
                ratio = otherRatio
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ColorGray.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 4.dp)
    ) {
        WMText(
            text = title,
            style = typography.titleSmall.copy(
                color = ColorGray.Gray_500,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row {
            Box(
                modifier = Modifier
                    .size(150.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(130.dp)) {
                    val strokeWidth = 20.dp.toPx()
                    val arcStroke = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    val emptyStroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

                    var startAngle = -90f
                    if (processedCategories.isEmpty()) {
                        drawArc(
                            color = ColorGray.Gray_100,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = emptyStroke
                        )
                    } else {
                        processedCategories.forEachIndexed { index, category ->
                            val sweepAngle = category.ratio * 360f
                            if (sweepAngle > 0f) {
                                drawArc(
                                    color = colors[index % colors.size],
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    style = arcStroke
                                )
                            }
                            startAngle += sweepAngle
                        }
                    }
                }

                WMText(
                    text = totalLabel,
                    style = typography.bodyLarge.copy(
                        color = ColorGray.Gray_500,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                processedCategories.forEachIndexed { index, category ->
                    key(category.categoryName) {
                        StatusLegendItem(
                            category = category,
                            color = colors[index % colors.size]
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusLegendItem(
    category: CategoryDiffInfoVo,
    color: Color,
) {
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = category.categoryIcon,
                style = typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = category.categoryName,
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )

            val rate = remember(category.ratio) { (category.ratio * 100).roundToInt() }
            WMText(
                text = "${rate}%",
                style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
