package com.jie.wealthmate.feature.menu.categorySetting.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed

data class CategoryItemData(
    val id: Int,
    val icon: ImageVector,
    val label: String,
    val backgroundColor: Color,
    val sort: Int,
    val largeCategory: LargeCategoryEnum,
)


enum class LargeCategoryEnum(val label: String, val backgroundColor: Color) {
    INCOME(
        label = "수입",
        backgroundColor = ColorBlue.Blue_100
    ),
    EXPENSES(
        label = "지출",
        backgroundColor = ColorRed.Red_100
    ),
    SAVING(
        label = "저축",
        backgroundColor = ColorPrimary.Primary_200
    ),
    ;
}