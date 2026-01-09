package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WMButton(
    text: String,
    buttonStyle: ButtonStyle = ButtonStyle.ELEVATED,
    enable: Boolean = true,
    onClick: () -> Unit,
) {
    when (buttonStyle) {
        ButtonStyle.ELEVATED -> {
            ElevatedButton(
                onClick = onClick,
                enabled = enable,
            ) {
                WMText(
                    text = text,
                    style = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (enable) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.FILLED -> {
            Button(
                onClick = onClick,
                enabled = enable,
            ) {
                WMText(
                    text = text,
                    style = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (enable) ColorGray.White else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.TONAL -> {
            FilledTonalButton(
                onClick = onClick,
                enabled = enable,
            ) {
                WMText(
                    text = text,
                    style = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (enable) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enable,
            ) {
                WMText(
                    text = text,
                    style = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (enable) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }

        ButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                enabled = enable
            ) {
                WMText(
                    text = text,
                    style = Typography().bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (enable) ColorGray.Gray_700 else ColorGray.Gray_300
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun WMButtonPreview() {
    WMTheme() {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WMButton(
                    text = "ELEVATED",
                    buttonStyle = ButtonStyle.ELEVATED,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    onClick = {}
                )
                WMButton(
                    text = "TONAL",
                    buttonStyle = ButtonStyle.TONAL,
                    onClick = {}
                )
                WMButton(
                    text = "OUTLINED",
                    buttonStyle = ButtonStyle.OUTLINED,
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
                    enable = false,
                    onClick = {}
                )
                WMButton(
                    text = "FILLED",
                    buttonStyle = ButtonStyle.FILLED,
                    enable = false,
                    onClick = {}
                )
                WMButton(
                    text = "TONAL",
                    buttonStyle = ButtonStyle.TONAL,
                    enable = false,
                    onClick = {}
                )
                WMButton(
                    text = "OUTLINED",
                    buttonStyle = ButtonStyle.OUTLINED,
                    enable = false,
                    onClick = {}
                )
                WMButton(
                    text = "TEXT",
                    buttonStyle = ButtonStyle.TEXT,
                    enable = false,
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