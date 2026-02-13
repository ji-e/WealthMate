package com.jie.wealthmate.component.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
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
    val focusManager = LocalFocusManager.current

    TopAppBar(
        modifier = Modifier.padding(horizontal = 10.dp),
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
            if (readingItem == null) {
                Box(modifier = Modifier.width(8.dp))
            } else {
                WMIconButton(
                    iconRes = readingItem.iconRes,
                    tint = readingItem.tint,
                    onClick = {
                        focusManager.clearFocus()
                        readingItem.action()
                    }
                )
            }
        },
        actions = {
            trailingCustomItem?.content()
            trailingItem?.forEach {
                WMIconButton(
                    iconRes = it.iconRes,
                    tint = it.tint,
                    onClick = {
                        focusManager.clearFocus()
                        it.action()
                    }
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
private fun WMTopBarPreview() {
    WMTheme {
        WMTopBar(
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