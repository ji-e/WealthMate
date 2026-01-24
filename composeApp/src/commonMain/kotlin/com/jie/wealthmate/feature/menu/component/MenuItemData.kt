package com.jie.wealthmate.feature.menu.component

/**
 * Menu 항목을 정의하는 sealed class
 */
sealed class MenuItemData(
    val label: String,
    val items: List<MenuEnum>,
) {
    object Management : MenuItemData(
        label = "관리",
        items = MenuEnum.managementMenu
    )

    companion object Companion {
        val menuItems = listOf(
            Management,
        )
    }
}