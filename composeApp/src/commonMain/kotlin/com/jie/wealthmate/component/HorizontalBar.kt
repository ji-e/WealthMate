package com.jie.wealthmate.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme

@Composable
fun HorizontalBar(
    spent: Long,
    total: Long,
    changeColorRatio: Float = 0.8f,
    firstBarColor: Color,
    secondBarColor: Color? = null,
    isEnabled: Boolean = true,
    isAnimated: Boolean = true,
    modifier: Modifier = Modifier,
    onAnimatedSpentChange: (Float) -> Unit = {},
) {
    // 초기 애니메이션 시작 여부 제어. 애니메이션이 비활성화된 경우 즉시 시작 상태로 설정
    var isStarted by remember { mutableStateOf(isAnimated.not()) }

    LaunchedEffect(Unit) {
        if (isAnimated) {
            isStarted = true
        }
    }

    // 금액 애니메이션 (0 -> spent)
    val animatedSpent by animateFloatAsState(
        targetValue = if (isStarted) spent.toFloat() else 0f,
        animationSpec =
            if (isAnimated) tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            else snap(),
        label = "SpentAnimation"
    )

    // 애니메이션되는 금액을 외부로 전달 (예: 카운팅되는 텍스트 UI 업데이트용)
    LaunchedEffect(animatedSpent) {
        onAnimatedSpentChange(animatedSpent)
    }

    // 바의 비율 계산 (0.0 ~ 1.0)
    val barRatio = remember(animatedSpent, total) {
        if (total > 0) (animatedSpent / total.toFloat()).coerceIn(0f, 1f)
        else 0f
    }

    // 상태에 따른 색상 결정
    val targetColor = when {
        isEnabled.not() -> ColorSetting.DisabledBackground
        barRatio >= changeColorRatio -> secondBarColor ?: firstBarColor
        else -> firstBarColor
    }

    // 색상 변경 시 부드러운 전환
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(1000),
        label = "ColorAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
            .background(ColorGray.Gray_200)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(barRatio)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(animatedColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HorizontalBarPreview() {
    WMTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // 일반 상태 (50% 사용)
                HorizontalBar(
                    spent = 50000,
                    total = 100000,
                    firstBarColor = ColorBlue.Blue_200,
                )

                // 경고 상태 (80% 사용, 색상 변경)
                HorizontalBar(
                    spent = 90000,
                    total = 100000,
                    changeColorRatio = 0.8f,
                    firstBarColor = ColorBlue.Blue_200,
                    secondBarColor = ColorRed.Red_200
                )

                // 비활성화 상태
                HorizontalBar(
                    spent = 50000,
                    total = 100000,
                    firstBarColor = ColorBlue.Blue_200,
                    secondBarColor = ColorRed.Red_200,
                    isEnabled = false
                )
            }
        }
    }
}
