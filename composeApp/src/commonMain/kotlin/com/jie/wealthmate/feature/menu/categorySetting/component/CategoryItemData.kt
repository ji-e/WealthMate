package com.jie.wealthmate.feature.menu.categorySetting.component

import androidx.compose.ui.graphics.Color
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed

data class CategoryItemData(
    val id: Long,
    val icon: String,
    val label: String,
    val sort: Long,
    val isFixed: Boolean = false,
    val largeCategory: LargeCategoryEnum,
)


enum class LargeCategoryEnum(
    val label: String,
    val backgroundColor: Color,
    val tempMiddleCategoryLabel: String,
    val tempTagLabel: String,
) {
    INCOME(
        label = "수입",
        backgroundColor = ColorBlue.Blue_100,
        tempMiddleCategoryLabel = "급여",
        tempTagLabel = "상여금"
    ),
    EXPENSES(
        label = "지출",
        backgroundColor = ColorRed.Red_100,
        tempMiddleCategoryLabel = "식비",
        tempTagLabel = "외식"
    ),
    SAVING(
        label = "저축",
        backgroundColor = ColorPrimary.Primary_200,
        tempMiddleCategoryLabel = "정기 저축",
        tempTagLabel = "주책 청약"
    ),
    ;

    companion object {
        fun creator(name: String): LargeCategoryEnum {
            return LargeCategoryEnum.entries.find { it.name == name } ?: INCOME
        }

        fun creatorFromMenu(label: String): LargeCategoryEnum {
            return LargeCategoryEnum.entries.find { label.contains(it.label) } ?: INCOME

        }
    }
}