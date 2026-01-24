package com.jie.wealthmate.feature.menu.component

/**
 * Menu 항목을 정의하는 sealed class
 */
sealed class MenuItem(
    val label: String,
    val items: List<MenuEnum>,
) {
    object Management : MenuItem(
        label = "관리",
        items = MenuEnum.managementMenu
    )

    companion object {
        val menuItems = listOf(
            Management,
        )
    }
}