package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.theme.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_check_circle

@Composable
fun WMCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    label: String? = null,
    labelStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .noRippleClickable(
                enabled = enabled,
                onClick = { onCheckedChange(checked.not()) }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_check_circle),
            contentDescription = null,
            modifier = iconModifier.size(24.dp),
            tint = when {
                enabled.not() -> ColorSetting.DisabledBackground
                checked -> ColorSetting.Primary
                else -> ColorSetting.Empty
            }
        )

        label?.let {
            WMText(
                text = it,
                style = labelStyle,
                color = if (enabled) ColorSetting.Default else ColorSetting.DisabledContent
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun WMCheckBoxPreview() {
    WMTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText("Unchecked State", fontWeight = FontWeight.Bold)
                WMCheckBox(
                    label = "기본 체크해제",
                    checked = false,
                    onCheckedChange = {}
                )
                WMCheckBox(
                    label = "비활성 체크해제",
                    checked = false,
                    enabled = false,
                    onCheckedChange = {}
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText("Checked State", fontWeight = FontWeight.Bold)
                WMCheckBox(
                    label = "기본 체크됨",
                    checked = true,
                    onCheckedChange = {}
                )
                WMCheckBox(
                    label = "비활성 체크됨",
                    checked = true,
                    enabled = false,
                    onCheckedChange = {}
                )
            }
        }
    }
}
