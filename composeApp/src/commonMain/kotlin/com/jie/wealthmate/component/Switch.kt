package com.jie.wealthmate.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.theme.noRippleClickable

@Composable
fun WMSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
    enabled: Boolean = true,
    switchSize: SwitchSize = SwitchSize.MEDIUM,
) {
    val trackColor by animateColorAsState(
        targetValue = when {
            enabled.not() -> ColorSetting.DisabledBackground
            checked -> ColorSetting.Primary
            else -> ColorSetting.EmptyContent
        },
        animationSpec = tween(durationMillis = 300)
    )
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        modifier = modifier.noRippleClickable(enabled = enabled) { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        label?.let {
            WMText(
                text = it,
                style = labelStyle,
                color = if (enabled) ColorSetting.Default else ColorSetting.EmptyContent
            )
        }

        val height = switchSize.height
        val width = height * 1.8f
        val thumbSize = height * 0.65f
        val padding = (height - thumbSize) / 2

        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(trackColor)
                .padding(padding),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = (width - thumbSize - padding * 2) * thumbOffset)
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(ColorGray.White)
            )
        }
    }
}

enum class SwitchSize(val height: Dp) {
    X_SMALL(height = 16.dp),
    SMALL(height = 20.dp),
    MEDIUM(height = 28.dp),
    LARGE(height = 36.dp),
}

@Preview(showBackground = true)
@Composable
private fun WMSwitchPreview() {
    WMTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText("Switch Sizes", fontWeight = FontWeight.Bold)
                SwitchSize.entries.forEach { size ->
                    WMSwitch(
                        label = "Size ${size.name}",
                        checked = true,
                        switchSize = size,
                        onCheckedChange = {}
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText("States", fontWeight = FontWeight.Bold)
                WMSwitch(
                    label = "Unchecked",
                    checked = false,
                    onCheckedChange = {}
                )
                WMSwitch(
                    label = "Disabled Checked",
                    checked = true,
                    enabled = false,
                    onCheckedChange = {}
                )
                WMSwitch(
                    label = "Disabled Unchecked",
                    checked = false,
                    enabled = false,
                    onCheckedChange = {}
                )
            }
        }
    }
}
