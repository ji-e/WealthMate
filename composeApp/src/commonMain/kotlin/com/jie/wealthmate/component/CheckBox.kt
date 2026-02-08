package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_check_circle
import wealthmate.composeapp.generated.resources.ic_circle_outline

@Composable
fun WMCheckBox(
    modifier: Modifier = Modifier,
    label: String? = null,
    labelStyle: TextStyle = Typography().titleMedium.copy(fontWeight = FontWeight.Medium),
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier.noRippleClickable { onCheckedChange(checked.not()) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(if (checked) Res.drawable.ic_check_circle else Res.drawable.ic_circle_outline),
            contentDescription = null,
            modifier = Modifier
                .noRippleClickable(enabled) { onCheckedChange(checked.not()) }
                .padding(end = 4.dp)
                .size(24.dp),
            tint = when {
                enabled.not() -> ColorGray.Gray_100
                checked -> ColorPrimary.Primary_500
                else -> ColorGray.Gray_300
            }
        )

        label?.let {
            WMText(
                text = it,
                style = labelStyle,
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