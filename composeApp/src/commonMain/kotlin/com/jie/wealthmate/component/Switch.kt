package com.jie.wealthmate.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.noRippleClickable

@Composable
fun WMSwitch(
    modifier: Modifier = Modifier,
    label: String? = null,
    labelStyle: TextStyle = Typography().titleMedium.copy(fontWeight = FontWeight.Medium),
    checked: Boolean,
    enabled: Boolean = true,
    switchSize: SwitchSize = SwitchSize.MEDIUM,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    val trackColor by animateColorAsState(
        targetValue = when {
            enabled.not() -> ColorGray.Gray_100
            checked -> ColorPrimary.Primary_500
            else -> ColorGray.Gray_300
        },
        animationSpec = tween(durationMillis = 300)
    )
    val offset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        modifier = modifier.noRippleClickable { onCheckedChange(checked.not()) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val height = switchSize.height
        val width = height * 1.8f
        val thumbSize = height * 0.7f // thumb는 height의 70%
        val padding = (height - thumbSize) / 2

        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(trackColor)
                .clickable(enabled) { onCheckedChange(!checked) }
                .padding(padding),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = (width - thumbSize - padding * 2) * offset)
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(ColorGray.White)
            )
        }
    }

    label?.let {
        WMText(
            text = it,
            style = labelStyle,
        )
    }
}


enum class SwitchSize(val height: Dp) {
    X_SMALL(height = 20.dp),
    SMALL(height = 24.dp),
    MEDIUM(height = 32.dp),
    LARGE(height = 40.dp),
    ;
}