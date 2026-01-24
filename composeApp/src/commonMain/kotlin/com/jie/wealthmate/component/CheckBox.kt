package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.jie.wealthmate.theme.ColorGray
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WMCheckBox(
    modifier: Modifier = Modifier,
    label: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors().copy(
                checkedCheckmarkColor = ColorGray.White,
                disabledCheckedBoxColor = ColorGray.Gray_100,
                disabledBorderColor = ColorGray.Gray_100,
                disabledUncheckedBorderColor = ColorGray.Gray_100,
                uncheckedBorderColor = ColorGray.Gray_300,
            )
        )

        label?.let {
            WMText(
                text = it,
                style = Typography().titleMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun WMCheckBoxPreview() {
    Column {
        WMCheckBox(
            label = "체크박스",
            checked = false
        )
        WMCheckBox(
            label = "체크박스",
            checked = false,
            enabled = false
        )
        WMCheckBox(
            label = "checkBox",
            checked = true
        )
        WMCheckBox(
            label = "checkBox",
            checked = true,
            enabled = false
        )
    }
}