package com.jie.wealthmate.feature.menu.component

import org.jetbrains.compose.resources.DrawableResource

enum class MenuEnum(
    val route: String,
    val label: String,
    val icon: DrawableResource?,
) {
    INCOME_CATEGORY(
        route = "incomeCategory",
        label = "수입 카테고리",
        icon = null
    ),
    EXPENSES_CATEGORY(
        route = "expensesCategory",
        label = "지출 카테고리",
        icon = null
    ),
    SAVING_CATEGORY(
        route = "savingCategory",
        label = "저축 카테고리",
        icon = null
    ),
    ;

    companion object {
        val categoryMenu = listOf(
            INCOME_CATEGORY,
            EXPENSES_CATEGORY,
            SAVING_CATEGORY,
        )
    }
}