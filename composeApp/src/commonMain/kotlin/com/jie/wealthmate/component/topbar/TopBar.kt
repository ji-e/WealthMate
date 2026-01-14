package com.jie.wealthmate.component.topbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close
import wealthmate.composeapp.generated.resources.ic_menu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMTobBar(
    title: TopBarItem.Title,
    readingItem: TopBarItem.ReadingItem? = TopBarItem.ReadingItem(),
    trailingItem: List<TopBarItem.TrailingItem>? = null,
) {
    TopAppBar(
        title = {
            WMText(
                text = title.title,
                style = Typography().titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGray.Gray_700
                )
            )
        },
        navigationIcon = {
            readingItem?.let {
                WMIconButton(
                    iconRes = it.iconRes,
                    onClick = { it.action() }
                )
            }
        },
        actions = {
            trailingItem?.forEach {
                WMIconButton(
                    iconRes = it.iconRes,
                    onClick = { it.action() }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = ColorGray.White,
            navigationIconContentColor = ColorGray.Gray_700
        )
    )
}

@Composable
@Preview(showBackground = true)
private fun WMTobBarPreview() {
    WMTheme {
        WMTobBar(
            title = TopBarItem.Title("title"),
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