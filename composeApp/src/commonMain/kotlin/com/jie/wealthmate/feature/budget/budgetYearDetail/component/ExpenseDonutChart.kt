package com.jie.wealthmate.feature.budget.budgetYearDetail.component

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
import com.jie.wealthmate.feature.budget.budgetYearDetail.CategoryExpense
import com.jie.wealthmate.theme.ColorChart
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.formatWithCommas
import kotlin.math.roundToInt

@Composable
fun ExpenseDonutChart(
    year: String,
    categories: List<CategoryExpense>,
    totalExpense: Long,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val colors = remember { ColorChart.getCategoryChartColors() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WMText(
            text = "${year}년 최다 변동지출 카테고리",
            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val strokeWidth = 24.dp.toPx()
                // Stroke 스타일을 루프 밖에서 정의하여 재사용
                val arcStroke = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                val emptyStroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

                var startAngle = -90f
                if (categories.isEmpty()) {
                    drawArc(
                        color = ColorGray.Gray_100,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = emptyStroke
                    )
                } else {
                    categories.forEachIndexed { index, category ->
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WMText(
                    text = "총 변동지출",
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500)
                )
                WMText(
                    text = "${totalExpense.formatWithCommas()}원",
                    style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Legend
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            categories.forEachIndexed { index, category ->
                key(category.name) {
                    CategoryExpenseItem(
                        category = category,
                        color = colors[index % colors.size]
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun CategoryExpenseItem(
    category: CategoryExpense,
    color: Color,
) {
    val typography = MaterialTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = category.icon,
                style = typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = category.name,
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
            )

            val rate = remember(category.ratio) { (category.ratio * 100).roundToInt() }
            WMText(
                text = "${rate}%",
                style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        WMText(
            text = "${category.amount.formatWithCommas()}원",
            style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1
        )
    }
}
