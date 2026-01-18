package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WMButton(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String,
    buttonStyle: ButtonStyle = ButtonStyle.FILLED,
    buttonSize: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true,
    isRounded: Boolean = false,
    onClick: () -> Unit,
) {

    val defaultColor = ColorGray.Gray_700
    val disabledColor = ColorGray.Gray_300

    when (buttonStyle) {
        ButtonStyle.ELEVATED -> {
            ElevatedButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = modifier.height(buttonSize.buttonHeight),
                shape = if (isRounded) ButtonDefaults.elevatedShape else RoundedCornerShape(8.dp),
                colors = ButtonDefaults.elevatedButtonColors().copy(
                    contentColor = defaultColor,
                    disabledContentColor = disabledColor
                )
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                )
            }
        }

        ButtonStyle.FILLED -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = modifier.height(buttonSize.buttonHeight),
                shape = if (isRounded) ButtonDefaults.shape else RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors().copy(
                    contentColor = ColorGray.White,
                    disabledContentColor = disabledColor
                )
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                )
            }
        }

        ButtonStyle.TONAL -> {
            FilledTonalButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = modifier.height(buttonSize.buttonHeight),
                shape = if (isRounded) ButtonDefaults.filledTonalShape else RoundedCornerShape(8.dp),
                colors = ButtonDefaults.filledTonalButtonColors().copy(
                    contentColor = defaultColor,
                    disabledContentColor = disabledColor
                )
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                )
            }
        }

        ButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = modifier.height(buttonSize.buttonHeight),
                shape = if (isRounded) ButtonDefaults.outlinedShape else RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = defaultColor,
                    disabledContentColor = disabledColor
                )
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                )
            }
        }

        ButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = modifier.height(buttonSize.buttonHeight),
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = defaultColor,
                    disabledContentColor = disabledColor
                )
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                )
            }
        }
    }
}

@Composable
fun WMIconButton(
    modifier: Modifier = Modifier,
    iconRes: DrawableResource,
    contentDescription: String? = null,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit,
) {

    IconButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(iconRes),
            modifier = modifier.size(24.dp),
            contentDescription = contentDescription,
            tint = tint
        )
    }

}

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
    Column() {
        WMShadowDivider()

        Spacer(modifier = Modifier.height(20.dp))

        WMButton(
            modifier = modifier,
            text = text,
            buttonStyle = buttonStyle,
            buttonSize = buttonSize,
            enabled = enabled,
            isRounded = isRounded,
            onClick = onClick,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun WMButtonPreview() {
    WMTheme() {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WMButton(
                    text = "ELEVATED",
                    buttonStyle = ButtonStyle.ELEVATED,
                    buttonSize = ButtonSize.X_SMALL,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.SMALL,
                    onClick = {}
                )
                WMButton(
                    text = "TONAL",
                    buttonStyle = ButtonStyle.TONAL,
                    buttonSize = ButtonSize.MEDIUM,
                    onClick = {}
                )
                WMButton(
                    text = "OUTLINED",
                    buttonStyle = ButtonStyle.OUTLINED,
                    buttonSize = ButtonSize.LARGE,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.X_LARGE,
                    onClick = {}
                )
                WMButton(
                    text = "TEXT",
                    buttonStyle = ButtonStyle.TEXT,
                    onClick = {}
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WMButton(
                    text = "ELEVATED",
                    buttonStyle = ButtonStyle.ELEVATED,
                    buttonSize = ButtonSize.X_SMALL,
                    enabled = false,
                    isRounded = true,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.SMALL,
                    enabled = false,
                    isRounded = true,
                    onClick = {}
                )
                WMButton(
                    text = "TONAL",
                    buttonStyle = ButtonStyle.TONAL,
                    buttonSize = ButtonSize.MEDIUM,
                    enabled = false,
                    isRounded = true,
                    onClick = {}
                )
                WMButton(
                    text = "OUTLINED",
                    buttonStyle = ButtonStyle.OUTLINED,
                    buttonSize = ButtonSize.LARGE,
                    enabled = false,
                    isRounded = true,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.X_LARGE,
                    enabled = false,
                    isRounded = true,
                    onClick = {}
                )
                WMButton(
                    text = "TEXT",
                    buttonStyle = ButtonStyle.TEXT,
                    enabled = false,
                    isRounded = true,
                    onClick = {}
                )
            }
        }
    }
}


enum class ButtonStyle {
    ELEVATED,
    FILLED,
    TONAL,
    OUTLINED,
    TEXT
}

enum class ButtonSize(
    val buttonHeight: Dp,
    val textStyle: TextStyle,
) {
    X_SMALL(
        buttonHeight = 24.dp,
        textStyle = Typography().bodySmall.copy(fontWeight = FontWeight.Medium)
    ),
    SMALL(
        buttonHeight = 32.dp,
        textStyle = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium)
    ),
    MEDIUM(
        buttonHeight = 40.dp,
        textStyle = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium)
    ),
    LARGE(
        buttonHeight = 48.dp,
        textStyle = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium)
    ),
    X_LARGE(
        buttonHeight = 56.dp,
        textStyle = Typography().bodyLarge.copy(fontWeight = FontWeight.Medium)
    )
}