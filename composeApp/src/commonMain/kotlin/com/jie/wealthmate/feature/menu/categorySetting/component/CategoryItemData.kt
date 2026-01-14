package com.jie.wealthmate.feature.menu.categorySetting.component

import androidx.compose.ui.graphics.Color
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import org.jetbrains.compose.resources.DrawableResource

data class CategoryItemData(
    val id: Int,
    val icon: DrawableResource,
    val label: String,
    val backgroundColor: Color,
    val sort: Int,
    val largeCategory: LargeCategoryEnum,
)


enum class LargeCategoryEnum(
    val label: String,
    val backgroundColor: Color,
    val tempMiddleCategoryLabel: String,
) {
    INCOME(
        label = "수입",
        backgroundColor = ColorBlue.Blue_100,
        tempMiddleCategoryLabel = "급여",
    ),
    EXPENSES(
        label = "지출",
        backgroundColor = ColorRed.Red_100,
        tempMiddleCategoryLabel = "식비",
    ),
    SAVING(
        label = "저축",
        backgroundColor = ColorPrimary.Primary_200,
        tempMiddleCategoryLabel = "정기 저축"
    ),
    ;
}