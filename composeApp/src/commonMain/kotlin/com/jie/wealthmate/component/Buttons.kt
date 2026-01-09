package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    text: String,
    buttonStyle: ButtonStyle = ButtonStyle.FILLED,
    buttonSize: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    when (buttonStyle) {
        ButtonStyle.ELEVATED -> {
            ElevatedButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(buttonSize.buttonHeight)
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                    color = if (enabled) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.FILLED -> {
            Button(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(buttonSize.buttonHeight)
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                    color = if (enabled) ColorGray.White else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.TONAL -> {
            FilledTonalButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(buttonSize.buttonHeight)
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                    color = if (enabled) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(buttonSize.buttonHeight),

                ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                    color = if (enabled) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(buttonSize.buttonHeight)
            ) {
                WMText(
                    text = text,
                    style = buttonSize.textStyle,
                    color = if (enabled) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }
    }
}

@Composable
fun WMIconButton(
    iconRes: DrawableResource,
    contentDescription: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {

    IconButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(iconRes),
            modifier = Modifier.size(24.dp),
            contentDescription = contentDescription
        )
    }

}

@Composable
@Preview(showBackground = true)
fun WMButtonPreview() {
    WMTheme() {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WMButton(
                    text = "ELEVATED",
                    buttonStyle = ButtonStyle.ELEVATED,
                    buttonSize = ButtonSize.X_SMALL,
                    enabled = false,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.SMALL,
                    enabled = false,
                    onClick = {}
                )
                WMButton(
                    text = "TONAL",
                    buttonStyle = ButtonStyle.TONAL,
                    buttonSize = ButtonSize.MEDIUM,
                    enabled = false,
                    onClick = {}
                )
                WMButton(
                    text = "OUTLINED",
                    buttonStyle = ButtonStyle.OUTLINED,
                    buttonSize = ButtonSize.LARGE,
                    enabled = false,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.X_LARGE,
                    enabled = false,
                    onClick = {}
                )
                WMButton(
                    text = "TEXT",
                    buttonStyle = ButtonStyle.TEXT,
                    enabled = false,
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