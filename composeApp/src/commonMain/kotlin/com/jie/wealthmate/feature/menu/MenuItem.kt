package com.jie.wealthmate.feature.menu

import org.jetbrains.compose.resources.DrawableResource

/**
 * Menu 항목을 정의하는 sealed class
 */
sealed class MenuItem(
    val route: String,
    val label: String,
    val icon: DrawableResource?,
) {
    object IncomeCategory : MenuItem(
        route = "incomeCategory",
        label = "수입 카테고리",
        icon = null
    )
}