package com.jie.wealthmate.component.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close
import wealthmate.composeapp.generated.resources.ic_menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMTopBar(
    title: TopBarItem.Title,
    readingItem: TopBarItem.ReadingItem? = TopBarItem.ReadingItem(),
    trailingItem: List<TopBarItem.TrailingItem>? = null,
    trailingCustomItem: TopBarItem.TrailingCustomItem? = null,
) {
    val horizontalDp = 12.dp
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorGray.White)
            .padding(horizontal = horizontalDp),
        title = {
            WMText(
                text = title.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = ColorSetting.Default
            )
        },
        navigationIcon = {
            if (readingItem == null) {
                Box(modifier = Modifier.width(horizontalDp))
            } else {
                WMIconButton(
                    iconRes = readingItem.iconRes,
                    tint = readingItem.tint,
                    onClick = readingItem.action
                )
            }
        },
        actions = {
            trailingCustomItem?.content?.invoke()
            trailingItem?.forEach { item ->
                WMIconButton(
                    iconRes = item.iconRes,
                    tint = item.tint,
                    onClick = item.action
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = ColorGray.White,
            navigationIconContentColor = ColorSetting.Default,
            titleContentColor = ColorSetting.Default,
            actionIconContentColor = ColorSetting.Default
        )
    )
}

@Composable
@Preview(showBackground = true)
private fun WMTopBarPreview() {
    WMTheme {
        WMTopBar(
            title = TopBarItem.Title("상세 내역"),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_menu
                ),
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_close
                )
            )
        )
    }
}
