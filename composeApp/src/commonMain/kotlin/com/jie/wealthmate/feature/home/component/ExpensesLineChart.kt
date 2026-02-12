package com.jie.wealthmate.feature.home.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.today
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number


@Composable
fun ExpensesLineChart(
    modifier: Modifier = Modifier,
    statusType: StatusType,
    currentData: List<Float?>,
    lastData: List<Float>,
) {
    val typography = MaterialTheme.typography
    var triggerAnimation by remember { mutableStateOf(false) }
    val animateProgress by animateFloatAsState(
        targetValue = if (triggerAnimation) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "LineChartAnimation"
    )

    LaunchedEffect(Unit) {
        triggerAnimation = true
    }

    // 데이터 전처리 및 인덱스 계산 최적화
    val validDataCount = remember(currentData) { currentData.count { it != null } }
    val todayIndex = remember(statusType, currentData.size) {
        val rawIndex = when (statusType) {
            StatusType.WEEK -> today.dayOfWeek.isoDayNumber
            StatusType.MONTH -> today.day
            StatusType.YEAR -> today.month.number
        } -1
        rawIndex.coerceIn(0, currentData.size - 1)
    }
    val todayDataIndex = remember(currentData, todayIndex) {
        currentData.take(todayIndex + 1).count { it != null }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        WMText(
            text = "${statusType.lastLabel} 대비 지출 흐름",
            style = typography.titleSmall.copy(color = ColorGray.Gray_500),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, color = ColorGray.Gray_50, shape = RoundedCornerShape(8.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                val horizontalPadding = 16f
                val verticalPadding = 16f

                val usableWidth = size.width - (horizontalPadding * 2)
                val usableHeight = size.height - (verticalPadding * 2)

                val dataCount = currentData.size
                val spacing = if (dataCount > 1) usableWidth / (dataCount - 1) else 0f

                fun getSafeX(index: Int): Float = (index * spacing) + horizontalPadding
                fun getSafeY(value: Float): Float = (usableHeight - (value * usableHeight)) + verticalPadding

                // 1. 지난 데이터 (점선)
                val lastPath = Path().apply {
                    lastData.forEachIndexed { i, value ->
                        val x = getSafeX(i)
                        val y = getSafeY(value)
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }
                drawPath(
                    lastPath,
                    ColorGray.Gray_200,
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )
                )

                // 2. 이번 데이터 (실선) - 애니메이션 적용
                val pointsToShow = (validDataCount * animateProgress).toInt()
                val currentPath = Path()
                var isFirstPoint = true
                var drawnPoints = 0

                currentData.forEachIndexed { i, value ->
                    if (value != null && drawnPoints < pointsToShow) {
                        val x = getSafeX(i)
                        val y = getSafeY(value)
                        if (isFirstPoint) {
                            currentPath.moveTo(x, y)
                            isFirstPoint = false
                        } else {
                            currentPath.lineTo(x, y)
                        }
                        drawnPoints++

                        // 애니메이션 중간 지점 처리 (마지막 포인트로 가는 도중)
                        if (drawnPoints == pointsToShow && pointsToShow < validDataCount) {
                            val nextNonNullIndex = currentData.withIndex()
                                .drop(i + 1)
                                .firstOrNull { it.value != null }
                                ?.index

                            nextNonNullIndex?.let { nextIdx ->
                                currentData[nextIdx]?.let { nextValue ->
                                    val fraction = (validDataCount * animateProgress) - pointsToShow
                                    val partialX = x + (getSafeX(nextIdx) - x) * fraction
                                    val partialY = y + (getSafeY(nextValue) - y) * fraction
                                    currentPath.lineTo(partialX, partialY)
                                }
                            }
                        }
                    } else if (value == null) {
                        isFirstPoint = true
                    }
                }

                drawPath(
                    currentPath,
                    ColorRed.Red_300,
                    style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // 3. 오늘 지점 강조
                if (todayDataIndex <= pointsToShow) {
                    currentData.getOrNull(todayIndex)?.let { todayValue ->
                        drawCircle(
                            color = ColorRed.Red_300,
                            radius = 8f,
                            center = Offset(getSafeX(todayIndex), getSafeY(todayValue))
                        )
                    }
                }
            }
        }
    }
}
