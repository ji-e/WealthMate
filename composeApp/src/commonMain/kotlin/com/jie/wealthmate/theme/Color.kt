package com.jie.wealthmate.theme

import androidx.compose.ui.graphics.Color


object ColorSetting {
    val DisabledBackground = ColorGray.Gray_100
    val DisabledContent = ColorGray.Gray_300
    val Default = ColorGray.Gray_700
    val Info = ColorGray.Gray_500
    val EmptyBackground = ColorGray.Gray_50
    val EmptyContent = ColorGray.Gray_400
    val Error = ColorRed.Red_300
    val Success = ColorBlue.Blue_300
    val Primary = ColorPrimary.Primary_500
}

object ColorGray {
    val Gray_700 = Color(0xFF444444)
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

object ColorRed {
    val Red_300 = Color(0xFFF87171)
    val Red_200 = Color(0xFFFF9595)
    val Red_100 = Color(0xFFFFD5D5)
    val Red_50 = Color(0xFFFFF2F2)
    fun getColorList() = listOf(
        Pair("Red_300", Red_300),
        Pair("Red_200", Red_200),
        Pair("Red_100", Red_100),
        Pair("Red_50", Red_50)
    )
}

object ColorYellow {
    val Yellow_300 = Color(0xFFFBBF24)
}

object ColorBlue {
    val Blue_300 = Color(0xFF227EFF)
    val Blue_200 = Color(0xFF87B9FF)
    val Blue_100 = Color(0xFFBDD8FF)
    val Blue_50 = Color(0xFFEFF5FF)
    fun getColorList() = listOf(
        Pair("Blue_300", Blue_300),
        Pair("Blue_200", Blue_200),
        Pair("Blue_100", Blue_100),
        Pair("Blue_50", Blue_50)
    )
}

object ColorGroup {
    val Group_Lavender = Color(0xFFF3E8FF)
    val Group_Sky = Color(0xFFE8F3FF)
    val Group_Mint = Color(0xFFE8FFED)
    val Group_Cream = Color(0xFFFFF9E8)
    val Group_Rose = Color(0xFFFFE8F0)
    val Group_Purple = Color(0xFFF0E8FF)
    val Group_Cyan = Color(0xFFE8FFFF)
    val Group_Peach = Color(0xFFFFF3E8)
    val Group_Lime = Color(0xFFF3FFE8)
    val Group_Coral = Color(0xFFFFE8E8)

    fun getColorList() = listOf(
        Pair("Group_Lavender", Group_Lavender),
        Pair("Group_Sky", Group_Sky),
        Pair("Group_Mint", Group_Mint),
        Pair("Group_Cream", Group_Cream),
        Pair("Group_Rose", Group_Rose),
        Pair("Group_Purple", Group_Purple),
        Pair("Group_Cyan", Group_Cyan),
        Pair("Group_Peach", Group_Peach),
        Pair("Group_Lime", Group_Lime),
        Pair("Group_Coral", Group_Coral)
    )
}

object ColorCategory {
    val Category_Mint = Color(0xFF4ECDC4)      // 저축/수입
    val Category_Coral = Color(0xFFFF6B6B)     // 식비/생활비
    val Category_Sky = Color(0xFF4A90E2)       // 고정지출/주거비
    val Category_Lavender = Color(0xFFA8A4CE)  // 문화/여가
    val Category_Yellow = Color(0xFFFFD93D)    // 교통/통신비
    val Category_Ect = Color(0xFFDADEE4)

    fun getColorList() = listOf(
        Pair("Category_Mint", Category_Mint),
        Pair("Category_Coral", Category_Coral),
        Pair("Category_Sky", Category_Sky),
        Pair("Category_Lavender", Category_Lavender),
        Pair("Category_Yellow", Category_Yellow),
        Pair("Category_Ect", Category_Ect)
    )
}

object ColorChart {
    // 더 차분한 버전
    val Chart_Teal = Color(0xFF5EEAD4)        // 티ール
    val Chart_Pink = Color(0xFFF9A8D4)        // 연한 핑크
    val Chart_Blue = Color(0xFF7DD3FC)        // 아쿠아 블루
    val Chart_Purple = Color(0xFFD8B4FE)      // 연보라
    val Chart_Amber = Color(0xFFFCD34D)       // 앰버
    val Chart_Red = Color(0xFFFDA4AF)         // 연한 레드
    val Chart_Emerald = Color(0xFF6EE7B7)     // 에메랄드
    val Chart_Sage = Color(0xFFA7C4BC)        // 세이지 그린
    val Chart_Orange = Color(0xFFFDBA74)      // 연한 오렌지
    val Chart_Mint = Color(0xFF6EE7B7)        // 부드러운 민트
    val Chart_Coral = Color(0xFFFCA5A5)       // 코랄/피치
    val Chart_Sky = Color(0xFF93C5FD)         // 부드러운 하늘색
    val Chart_Lavender = Color(0xFFC4B5FD)    // 라벤더 (Primary와 유사 계열)
    val Chart_Butter = Color(0xFFFDE68A)      // 버터/레몬
    val Chart_Rose = Color(0xFFFBBCBB)        // 연한 로즈
    val Chart_Peach = Color(0xFFFFD4B8)       // 피치
    val Category_Ect = Color(0xFFDADEE4)

    fun getCategoryChartColors() = listOf(
        Chart_Rose,
        Chart_Sky,
        Chart_Lavender,
        Chart_Butter,
        Chart_Sage,
        Category_Ect
    )

    fun getPaymentMethodChartColors() = listOf(
        Chart_Peach,
        Chart_Purple,
        Chart_Amber,
        Category_Ect
    )


}