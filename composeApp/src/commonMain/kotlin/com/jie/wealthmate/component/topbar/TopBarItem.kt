package com.jie.wealthmate.component.topbar

import org.jetbrains.compose.resources.DrawableResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_back
import wealthmate.composeapp.generated.resources.ic_close

/**
 * TopBar 항목을 정의하는 sealed class
 */
sealed class TopBarItem {
    data class Title(
        val title: String,
    ) : TopBarItem()

    data class ReadingItem(
        val iconRes: DrawableResource = Res.drawable.ic_arrow_back,
        val action: () -> Unit = {},
    ) : TopBarItem()

    data class TrailingItem(
        val iconRes: DrawableResource = Res.drawable.ic_close,
        val action: () -> Unit = {},
    ) : TopBarItem()

}