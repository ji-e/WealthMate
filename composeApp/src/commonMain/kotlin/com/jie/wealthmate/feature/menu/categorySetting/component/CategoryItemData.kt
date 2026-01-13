package com.jie.wealthmate.feature.menu.categorySetting.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryItemData(
    val id: Int,
    val icon: ImageVector,
    val label: String,
    val backgroundColor: Color,
    val sort: Int,
    val largeCategory: LargeCategoryEnum,
)


enum class LargeCategoryEnum {
    INCOME,
    EXPENSES,
    SAVING,
    ;
}