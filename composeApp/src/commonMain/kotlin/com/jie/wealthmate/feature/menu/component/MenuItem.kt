package com.jie.wealthmate.feature.menu.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_keyboard_arrow_right

@Composable
fun MenuTitleItem(
    label: String,
) {
    WMText(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = ColorPrimary.Primary_700,
        modifier = Modifier
            .padding(bottom = Padding.SpacerXS)
            .padding(horizontal = Padding.BackgroundHorizontal)
    )
}

@Composable
fun MenuItem(
    menu: MenuEnum,
    isEnabled: Boolean = true,
    onClickMenu: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClickMenu() }
            .padding(horizontal = Padding.BackgroundHorizontal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = menu.label,
            style = MaterialTheme.typography.titleMedium,
            color = if (isEnabled) ColorSetting.Default else ColorSetting.DisabledContent,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(Res.drawable.ic_keyboard_arrow_right),
            contentDescription = menu.label,
            modifier = Modifier.size(24.dp),
            tint = ColorSetting.Info,
        )
    }
}