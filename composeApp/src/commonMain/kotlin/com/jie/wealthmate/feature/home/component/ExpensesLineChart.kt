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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.utils.today
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number

private const val ANIMATION_DURATION = 1000
private const val LAST_DATA_STROKE_WIDTH = 4f
private const val CURRENT_DATA_STROKE_WIDTH = 6f
private const val DASH_LENGTH = 10f
private const val TODAY_CIRCLE_RADIUS = 8f
private const val CHART_HEIGHT = 80

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
        animationSpec = tween(ANIMATION_DURATION, easing = FastOutSlowInEasing),
        label = "LineChartAnimation"
    )

    LaunchedEffect(Unit) {
        triggerAnimation = true
    }

    // 데이터 전처리 및 인덱스 계산 최적화
    val validDataCount = remember(currentData) { currentData.count { it != null } }
    val todayIndex = remember(statusType, currentData.size) {
        if (currentData.isEmpty()) {
            -1
        } else {
            val rawIndex = when (statusType) {
                StatusType.WEEK -> today.dayOfWeek.isoDayNumber
                StatusType.MONTH -> today.day
                StatusType.YEAR -> today.month.number
            } - 1
            rawIndex.coerceIn(0, currentData.size - 1)
        }
    }
    val todayDataIndex = remember(currentData, todayIndex) {
        if (todayIndex == -1) 0
        else currentData.take(todayIndex + 1).count { it != null }
    }

    // Path 객체 재사용을 위해 remember
    val lastPath = remember { Path() }
    val currentPath = remember { Path() }

    Column(modifier = modifier.fillMaxWidth()) {
        WMText(
            text = "${statusType.lastLabel} 대비 지출 흐름",
            style = typography.titleSmall,
            color = ColorSetting.Info,
        )

        WMSpacer(size = SpacerSize.SMALL)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = ColorSetting.DisabledBackground,
                    shape = Shapes.medium
                )
        ) {
            Canvas(
                modifier = Modifier
                    .padding(
                        vertical = Padding.ContainerVertical,
                        horizontal = Padding.ContainerHorizontal
                    )
                    .fillMaxWidth()
                    .height(CHART_HEIGHT.dp)
            ) {
                val horizontalPadding = 16f
                val verticalPadding = 16f

                val usableWidth = size.width - (horizontalPadding * 2)
                val usableHeight = size.height - (verticalPadding * 2)

                val dataCount = currentData.size
                val spacing = if (dataCount > 1) usableWidth / (dataCount - 1) else 0f

                fun getSafeX(index: Int): Float = (index * spacing) + horizontalPadding
                fun getSafeY(value: Float): Float =
                    (usableHeight - (value * usableHeight)) + verticalPadding

                // 1. 지난 데이터 (점선)
                lastPath.reset()
                lastData.forEachIndexed { i, value ->
                    val x = getSafeX(i)
                    val y = getSafeY(value)
                    if (i == 0) lastPath.moveTo(x, y) else lastPath.lineTo(x, y)
                }
                drawPath(
                    lastPath,
                    ColorSetting.DisabledContent,
                    style = Stroke(
                        width = LAST_DATA_STROKE_WIDTH,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(DASH_LENGTH, DASH_LENGTH))
                    )
                )

                // 2. 이번 데이터 (실선) - 애니메이션 적용
                currentPath.reset()
                val pointsToShow = (validDataCount * animateProgress).toInt()
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
                            val nextEntry = currentData.withIndex()
                                .drop(i + 1)
                                .firstOrNull { it.value != null }

                            nextEntry?.let { (nextIdx, nextValue) ->
                                if (nextValue != null) {
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
                    LargeCategoryEnum.EXPENSES.middleColor,
                    style = Stroke(
                        width = CURRENT_DATA_STROKE_WIDTH,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 3. 오늘 지점 강조
                if (todayIndex != -1 && todayDataIndex <= pointsToShow) {
                    currentData.getOrNull(todayIndex)?.let { todayValue ->
                        drawCircle(
                            color = LargeCategoryEnum.EXPENSES.middleColor,
                            radius = TODAY_CIRCLE_RADIUS,
                            center = Offset(getSafeX(todayIndex), getSafeY(todayValue))
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpensesLineChartPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        ExpensesLineChart(
            statusType = StatusType.WEEK,
            currentData = listOf(0.1f, 0.4f, 0.3f, 0.7f, null, 0.5f, 0.8f),
            lastData = listOf(0.2f, 0.3f, 0.5f, 0.4f, 0.6f, 0.7f, 0.5f)
        )
    }
}
