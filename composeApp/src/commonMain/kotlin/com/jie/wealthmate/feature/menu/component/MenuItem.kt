package com.jie.wealthmate.feature.menu.component

/**
 * Menu 항목을 정의하는 sealed class
 */
sealed class MenuItem(
    val label: String,
    val items: List<MenuEnum>,
) {
    object CategoryMenu : MenuItem(
        label = "카테고리 설정",
        items = MenuEnum.categoryMenu
    )

    companion object {
        val menuItems = listOf(
            CategoryMenu,
        )
    }
}