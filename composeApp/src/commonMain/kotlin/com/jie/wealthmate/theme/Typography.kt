package com.jie.wealthmate.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.wanted_sans_bold
import wealthmate.composeapp.generated.resources.wanted_sans_medium
import wealthmate.composeapp.generated.resources.wanted_sans_regular
import wealthmate.composeapp.generated.resources.wanted_sans_semi_bold


@Composable
fun wantedSansFontFamily(): FontFamily {
    // 프리뷰 환경(InspectionMode)에서 폰트 리소스 로드 시 발생하는 렌더링 오류를 방지하기 위해 분기 처리합니다.
    return if (LocalInspectionMode.current) {
        FontFamily.Default
    } else {
        FontFamily(
            Font(resource = Res.font.wanted_sans_bold, weight = FontWeight.Bold),
            Font(resource = Res.font.wanted_sans_semi_bold, weight = FontWeight.SemiBold),
            Font(resource = Res.font.wanted_sans_medium, weight = FontWeight.Medium),
            Font(resource = Res.font.wanted_sans_regular, weight = FontWeight.Normal)
        )
    }
}

/**
 * 폰트 패밀리를 주입받아 Typography를 생성하는 일반 함수입니다.
 */
fun getTypography(fontFamily: FontFamily): Typography {
    return Typography(
        displayLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 57.sp),
        displayMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 45.sp),
        displaySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 36.sp),
        headlineLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 32.sp),
        headlineMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 28.sp),
        headlineSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 24.sp),
        titleLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 22.sp),
        titleMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp),
        titleSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
        bodyLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        bodyMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodySmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
        labelLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
        labelMedium = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
        labelSmall = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp)
    )
}
