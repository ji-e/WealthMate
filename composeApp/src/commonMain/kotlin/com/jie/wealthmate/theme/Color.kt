package com.jie.wealthmate.theme

import androidx.compose.ui.graphics.Color


object ColorGray {
    val Gray_700 = Color(0xFF282828)
    val Gray_600 = Color(0xFF44464B)
    val Gray_500 = Color(0xFF636871)
    val Gray_400 = Color(0xFF91969E)
    val Gray_300 = Color(0xFFBFC4CB)
    val Gray_200 = Color(0xFFDADEE4)
    val Gray_100 = Color(0xFFEEF0F5)
    val Gray_50 = Color(0xFFF9F9FB)
    val White = Color(0xFFFFFFFF)
    val White_80 = Color(0xCCFFFFFF)

    fun getColorList() = listOf(
        Pair("Gray_700", Gray_700),
        Pair("Gray_600", Gray_600),
        Pair("Gray_500", Gray_500),
        Pair("Gray_400", Gray_400),
        Pair("Gray_300", Gray_300),
        Pair("Gray_200", Gray_200),
        Pair("Gray_100", Gray_100),
        Pair("Gray_50", Gray_50),
        Pair("White", White),
        Pair("White_80", White_80),
    )
}

object ColorPrimary {
    val Primary_700 = Color(0xFF7C3AED)
    val Primary_600 = Color(0xFF8B5CF6)
    val Primary_500 = Color(0xFFA78BFA)
    val Primary_400 = Color(0xFFC4B5DF)
    val Primary_300 = Color(0xFFF3E8FF)
    val Primary_200 = Color(0xFFFAF5FF)


    fun getColorList() = listOf(
        Pair("Primary_700", Primary_700),
        Pair("Primary_600", Primary_600),
        Pair("Primary_500", Primary_500),
        Pair("Primary_400", Primary_400),
        Pair("Primary_300", Primary_300),
        Pair("Primary_200", Primary_200),
    )
}
