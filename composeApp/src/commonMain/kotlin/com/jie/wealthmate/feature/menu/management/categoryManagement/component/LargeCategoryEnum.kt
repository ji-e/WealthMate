package com.jie.wealthmate.feature.menu.management.categoryManagement.component

import androidx.compose.ui.graphics.Color
import com.jie.wealthmate.theme.ColorBlue
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed

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
        backgroundColor = ColorPrimary.Primary_300,
        tempMiddleCategoryLabel = "정기 저축",
        tempTagLabel = "주책 청약"
    ),
    TRANSFER(
        label = "이제",
        backgroundColor = ColorPrimary.Primary_300,
        tempMiddleCategoryLabel = "이체",
        tempTagLabel = "이체"
    )
    ;

    companion object {
        fun creator(name: String?): LargeCategoryEnum {
            return LargeCategoryEnum.entries.find { it.name == name } ?: INCOME
        }
    }
}