package com.jie.wealthmate.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun WMTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isSystemInDarkTheme()) {
        // todo 다크모드 정의 필요
        darkColorScheme(
            primary = ColorPrimary.Primary_500,
            onPrimary = ColorPrimary.Primary_400,
            primaryContainer = ColorGray.Gray_700,
            onPrimaryContainer = ColorGray.Gray_700,
            secondaryContainer = ColorPrimary.Primary_300,
            outlineVariant = ColorGray.Gray_300,
            onSurface = ColorGray.Gray_300,
            surfaceContainerLow = ColorPrimary.Primary_200,
            background = ColorGray.White
        )
    } else {
        lightColorScheme(
            primary = ColorPrimary.Primary_500,
            onPrimary = ColorPrimary.Primary_400,
            primaryContainer = ColorGray.Gray_700,
            onPrimaryContainer = ColorGray.Gray_700,
            secondaryContainer = ColorPrimary.Primary_300,
            outlineVariant = ColorGray.Gray_300,
            onSurface = ColorGray.Gray_300,
            surfaceContainerLow = ColorPrimary.Primary_200,
            background = ColorGray.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        content()
    }
}