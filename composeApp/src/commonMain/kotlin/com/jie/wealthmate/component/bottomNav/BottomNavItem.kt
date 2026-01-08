package com.jie.wealthmate.component.bottomNav

import org.jetbrains.compose.resources.DrawableResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_calendar
import wealthmate.composeapp.generated.resources.ic_chart
import wealthmate.composeapp.generated.resources.ic_home
import wealthmate.composeapp.generated.resources.ic_menu

/**
 * Bottom Navigation 항목을 정의하는 sealed class
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: DrawableResource,
) {
    object Home : BottomNavItem(
        route = "home",
        label = "홈",
        icon = Res.drawable.ic_home
    )
    object Calendar : BottomNavItem(
        route = "calendar",
        label = "캘린더",
        icon = Res.drawable.ic_calendar
    )
    object Asset : BottomNavItem(
        route = "asset",
        label = "자산",
        icon = Res.drawable.ic_chart
    )
    object Menu : BottomNavItem(
        route = "menu",
        label = "메뉴",
        icon = Res.drawable.ic_menu
    )
}