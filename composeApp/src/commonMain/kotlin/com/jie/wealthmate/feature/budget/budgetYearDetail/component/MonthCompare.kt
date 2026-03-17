package com.jie.wealthmate.feature.budget.budgetYearDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.budget.budgetYearDetail.MonthlyComparison
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun MonthCompare(
    year: String,
    monthlyData: List<MonthlyComparison>,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val scrollState = rememberScrollState()

    val currentDateTime = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val currentYear = currentDateTime.year
    val currentMonth = currentDateTime.month.number
    val isThisYear = year == currentYear.toString()

    // Optimization: Pre-calculate values to avoid redundant work during recomposition
    val maxAmount = remember(monthlyData) {
        val maxVal = monthlyData
            .maxOfOrNull {
                maxOf(it.income, it.expense + it.fixedExpense, it.saving)
            }.default()
        if (maxVal == 0L) 1_000_000L else (maxVal * 1.2).toLong()
    }

    val averages = remember(monthlyData) { calculateAverages(monthlyData) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        WMText(
            text = "월별 추이",
            style = MaterialTheme.typography.titleSmall.copy(
                color = ColorGray.Gray_500,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(label = "수입", color = ColorBlue.Blue_200)
            LegendItem(label = "변동지출", color = ColorRed.Red_200)
            LegendItem(label = "고정지출", color = ColorGray.Gray_400)
            LegendItem(label = "저축", color = ColorPrimary.Primary_400)
        }

        // Chart Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.Bottom
        ) {
            monthlyData.forEach { data ->
                key(data.month) {
                    val isCurrentMonth = isThisYear && data.month == currentMonth
                    MonthBarItem(
                        data = data,
                        maxAmount = maxAmount,
                        isCurrentMonth = isCurrentMonth
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = ColorGray.Gray_100)
        Spacer(modifier = Modifier.height(12.dp))

        // Average Area
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AverageInfo(
                    label = "월 평균 수입",
                    amount = averages.income,
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(
                    modifier = Modifier.height(40.dp).padding(horizontal = 4.dp),
                    color = ColorGray.Gray_100
                )
                AverageInfo(
                    label = "월 평균 변동지출",
                    amount = averages.variableExpense,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                AverageInfo(
                    label = "월 평균 고정지출",
                    amount = averages.fixedExpense,
                    modifier = Modifier.weight(1f)
                )
                VerticalDivider(
                    modifier = Modifier.height(40.dp).padding(horizontal = 4.dp),
                    color = ColorGray.Gray_100
                )
                AverageInfo(
                    label = "월 평균 저축",
                    amount = averages.saving,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MonthBarItem(
    data: MonthlyComparison,
    maxAmount: Long,
    isCurrentMonth: Boolean,
) {
    val barMaxHeight = 160.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isCurrentMonth) ColorPrimary.Primary_200 else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.height(barMaxHeight),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Bar(
                amount = data.income,
                maxAmount = maxAmount,
                color = ColorBlue.Blue_200,
                totalHeight = barMaxHeight
            )
            Spacer(modifier = Modifier.width(4.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Bar(
                    amount = data.expense,
                    maxAmount = maxAmount,
                    color = ColorRed.Red_200,
                    totalHeight = barMaxHeight
                )
                if (data.expense > 0 && data.fixedExpense > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Bar(
                    amount = data.fixedExpense,
                    maxAmount = maxAmount,
                    color = ColorGray.Gray_400,
                    totalHeight = barMaxHeight
                )
            }

            Spacer(modifier = Modifier.width(4.dp))
            Bar(
                amount = data.saving,
                maxAmount = maxAmount,
                color = ColorPrimary.Primary_400,
                totalHeight = barMaxHeight
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        WMText(
            text = "${data.month}월",
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isCurrentMonth) ColorGray.Gray_700 else ColorGray.Gray_500,
                fontWeight = if (isCurrentMonth) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun Bar(
    amount: Long,
    maxAmount: Long,
    color: Color,
    totalHeight: Dp,
) {
    if (amount <= 0L) return
    val height = totalHeight * (amount.toFloat() / maxAmount).coerceIn(0.01f, 1f)
    Box(
        modifier = Modifier
            .width(6.dp)
            .height(height)
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(color)
    )
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        WMText(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AverageInfo(
    label: String,
    amount: Long,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WMText(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = ColorGray.Gray_500)
        )
        Spacer(modifier = Modifier.height(2.dp))
        WMText(
            text = "₩${amount.formatWithCommas()}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

private data class Averages(
    val income: Long,
    val variableExpense: Long,
    val fixedExpense: Long,
    val saving: Long,
)

private fun calculateAverages(data: List<MonthlyComparison>): Averages {
    var sumInc = 0L;
    var countInc = 0
    var sumVar = 0L;
    var countVar = 0
    var sumFix = 0L;
    var countFix = 0
    var sumSav = 0L;
    var countSav = 0

    for (item in data) {
        if (item.income > 0) {
            sumInc += item.income; countInc++
        }
        if (item.expense > 0) {
            sumVar += item.expense; countVar++
        }
        if (item.fixedExpense > 0) {
            sumFix += item.fixedExpense; countFix++
        }
        if (item.saving > 0) {
            sumSav += item.saving; countSav++
        }
    }

    return Averages(
        income = if (countInc > 0) sumInc / countInc else 0L,
        variableExpense = if (countVar > 0) sumVar / countVar else 0L,
        fixedExpense = if (countFix > 0) sumFix / countFix else 0L,
        saving = if (countSav > 0) sumSav / countSav else 0L
    )
}
