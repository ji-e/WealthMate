package com.jie.wealthmate.feature.menu.component

import org.jetbrains.compose.resources.DrawableResource

enum class MenuEnum(
    val route: String,
    val label: String,
    val title: String,
    val icon: DrawableResource?,
) {
    CATEGORY(
        route = "category",
        label = "카테고리",
        title = "카테고리 관리",
        icon = null,
    ),
    INCOME_CATEGORY(
        route = "incomeCategory",
        label = "수입 카테고리",
        title = "수입 카테고리 관리",
        icon = null
    ),
    EXPENSES_CATEGORY(
        route = "expensesCategory",
        label = "지출 카테고리",
        title = "지출 카테고리 관리",
        icon = null
    ),
    SAVING_CATEGORY(
        route = "savingCategory",
        label = "저축 카테고리",
        title = "저축 카테고리 관리",
        icon = null
    ),
    ;

    companion object {
        val managementMenu = listOf(
            CATEGORY
        )
    }
}