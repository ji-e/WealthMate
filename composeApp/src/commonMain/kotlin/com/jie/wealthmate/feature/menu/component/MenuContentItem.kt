package com.jie.wealthmate.feature.menu.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorPrimary

@Composable
fun MenuTitleItem(
    label: String,
) {
    WMText(
        text = label,
        style = Typography().bodySmall.copy(
            fontWeight = FontWeight.Bold,
            color = ColorPrimary.Primary_700,
        ),
        modifier = Modifier
            .padding(top = 40.dp, bottom = 4.dp)
            .padding(horizontal = 20.dp)
    )
}

@Composable
fun MenuContentItem(
    menu: MenuEnum,
    onClickMenu: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClickMenu() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        WMText(
            text = menu.label,
            style = Typography().titleMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}