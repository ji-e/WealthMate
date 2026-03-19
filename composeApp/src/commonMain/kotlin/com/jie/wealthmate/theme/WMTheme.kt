package com.jie.wealthmate.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density

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

    val focusManager = LocalFocusManager.current
    val currentDensity = LocalDensity.current
    val fixedDensity = remember(currentDensity) {
        Density(
            density = currentDensity.density,
            fontScale = 1f
        )
    }

    // 폰트 패밀리를 먼저 로드하고 Typography를 생성하여 주입합니다. (프리뷰 안정성 확보)
    val fontFamily = wantedSansFontFamily()
    val typography = remember(fontFamily) { getTypography(fontFamily) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography
    ) {
        CompositionLocalProvider(
            LocalRippleConfiguration provides RippleConfiguration(color = ColorPrimary.Primary_400),
            LocalDensity provides fixedDensity,
        ) {
            Column(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
                    .background(ColorGray.White)
            ) {
                content()
            }
        }
    }
}

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit,
) = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick,
        role = role
    )
}
