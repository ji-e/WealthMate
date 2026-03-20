package com.jie.wealthmate.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

/**
 * WealthMate 공통 버튼 컴포넌트
 */
@Composable
fun WMButton(
    modifier: Modifier = Modifier,
    text: String,
    buttonStyle: ButtonStyle = ButtonStyle.FILLED,
    buttonSize: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true,
    isRounded: Boolean = false,
    colors: ButtonColors? = null,
    onClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val shape =
        if (isRounded) {
            when (buttonStyle) {
                ButtonStyle.ELEVATED -> ButtonDefaults.elevatedShape
                ButtonStyle.FILLED -> ButtonDefaults.shape
                ButtonStyle.TONAL -> ButtonDefaults.filledTonalShape
                ButtonStyle.OUTLINED -> ButtonDefaults.outlinedShape
                ButtonStyle.TEXT -> ButtonDefaults.textShape
            }
        } else {
            Shapes.medium
        }

    val wrappedOnClick = {
        focusManager.clearFocus()
        onClick()
    }

    val contentPadding = PaddingValues(horizontal = 12.dp)
    val buttonModifier = modifier.height(buttonSize.buttonHeight)
    val textStyle = buttonSize.toTextStyle()

    when (buttonStyle) {
        ButtonStyle.ELEVATED -> {
            ElevatedButton(
                onClick = wrappedOnClick,
                enabled = enabled,
                contentPadding = contentPadding,
                modifier = buttonModifier,
                shape = shape,
                colors = colors ?: ButtonDefaults.elevatedButtonColors(
                    contentColor = ColorSetting.Default,
                    disabledContentColor = ColorSetting.DisabledContent
                )
            ) {
                WMText(
                    text = text,
                    style = textStyle,
                    color = LocalContentColor.current
                )
            }
        }

        ButtonStyle.FILLED -> {
            Button(
                onClick = wrappedOnClick,
                enabled = enabled,
                contentPadding = contentPadding,
                modifier = buttonModifier,
                shape = shape,
                colors = colors ?: ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    disabledContentColor = ColorSetting.DisabledContent
                )
            ) {
                WMText(
                    text = text,
                    style = textStyle,
                    color = LocalContentColor.current
                )
            }
        }

        ButtonStyle.TONAL -> {
            FilledTonalButton(
                onClick = wrappedOnClick,
                enabled = enabled,
                contentPadding = contentPadding,
                modifier = buttonModifier,
                shape = shape,
                colors = colors ?: ButtonDefaults.filledTonalButtonColors(
                    contentColor = ColorSetting.Default,
                    disabledContentColor = ColorSetting.DisabledContent
                )
            ) {
                WMText(
                    text = text,
                    style = textStyle,
                    color = LocalContentColor.current
                )
            }
        }

        ButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = wrappedOnClick,
                enabled = enabled,
                contentPadding = contentPadding,
                modifier = buttonModifier,
                shape = shape,
                colors = colors ?: ButtonDefaults.outlinedButtonColors(
                    contentColor = ColorSetting.Default,
                    disabledContentColor = ColorSetting.DisabledContent
                )
            ) {
                WMText(
                    text = text,
                    style = textStyle,
                    color = LocalContentColor.current
                )
            }
        }

        ButtonStyle.TEXT -> {
            TextButton(
                onClick = wrappedOnClick,
                enabled = enabled,
                contentPadding = contentPadding,
                modifier = buttonModifier,
                shape = shape,
                colors = colors ?: ButtonDefaults.textButtonColors(
                    contentColor = ColorSetting.Default,
                    disabledContentColor = ColorSetting.DisabledContent
                )
            ) {
                WMText(
                    text = text,
                    style = textStyle,
                    color = LocalContentColor.current
                )
            }
        }
    }
}

/**
 * 아이콘 버튼 컴포넌트
 * @param modifier IconButton 에 적용될 modifier
 * @param iconModifier Icon 에 적용될 modifier
 */
@Composable
fun WMIconButton(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconRes: DrawableResource,
    contentDescription: String? = null,
    enabled: Boolean = true,
    tint: Color = ColorSetting.Default,
    onClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    IconButton(
        onClick = {
            focusManager.clearFocus()
            onClick()
        },
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(iconRes),
            modifier = iconModifier.size(24.dp),
            contentDescription = contentDescription,
            tint = if (enabled) tint else ColorSetting.DisabledContent
        )
    }
}

/**
 * 화면 하단에 고정되는 형태의 버튼 (구분선 포함)
 */
@Composable
fun WMFloatingButton(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String,
    buttonStyle: ButtonStyle = ButtonStyle.FILLED,
    buttonSize: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true,
    isRounded: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Padding.BackgroundHorizontal)
            .fillMaxWidth()
    ) {
        WMShadowDivider()

        WMSpacer()

        WMButton(
            modifier = modifier,
            text = text,
            buttonStyle = buttonStyle,
            buttonSize = buttonSize,
            enabled = enabled,
            isRounded = isRounded,
            onClick = onClick
        )
        WMSpacer()
    }
}

/**
 * 메뉴 리스트 등에서 사용되는 화살표가 포함된 버튼
 */
@Composable
fun WMMenuButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() }
            .padding(horizontal = Padding.BackgroundHorizontal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = ColorSetting.DisabledContent,
        )
    }
}

enum class ButtonStyle {
    ELEVATED, FILLED, TONAL, OUTLINED, TEXT
}

enum class ButtonSize(val buttonHeight: Dp) {
    X_SMALL(24.dp),
    SMALL(32.dp),
    MEDIUM(40.dp),
    LARGE(48.dp),
    X_LARGE(56.dp)
}

@Composable
private fun ButtonSize.toTextStyle(): TextStyle {
    val baseStyle = when (this) {
        ButtonSize.X_SMALL -> MaterialTheme.typography.bodySmall
        ButtonSize.SMALL, ButtonSize.MEDIUM, ButtonSize.LARGE -> MaterialTheme.typography.bodyMedium
        ButtonSize.X_LARGE -> MaterialTheme.typography.bodyLarge
    }
    return baseStyle.copy(
        fontWeight = FontWeight.Medium,
        fontFamily = baseStyle.fontFamily
    )
}

@Preview(showBackground = true)
@Composable
private fun WMButtonPreview() {
    WMTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WMText("Button Styles", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WMButton(text = "Filled", onClick = {})
                WMButton(text = "Tonal", buttonStyle = ButtonStyle.TONAL, onClick = {})
                WMButton(text = "Outlined", buttonStyle = ButtonStyle.OUTLINED, onClick = {})
            }

            WMText("Button Sizes", fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ButtonSize.entries.forEach { size ->
                    WMButton(text = "Size ${size.name}", buttonSize = size, onClick = {})
                }
            }

            WMText("Disabled & Rounded", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WMButton(text = "Disabled", enabled = false, onClick = {})
                WMButton(text = "Rounded", isRounded = true, onClick = {})
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WMMenuButtonPreview() {
    WMTheme {
        Column {
            WMMenuButton(label = "메뉴 항목 1", onClick = {})
            WMMenuButton(label = "메뉴 항목 2", onClick = {})
        }
    }
}
