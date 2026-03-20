package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.jie.wealthmate.theme.Padding

@Composable
fun WMSpacer(
    modifier: Modifier = Modifier,
    size: SpacerSize = SpacerSize.MEDIUM,
) {
    Spacer(
        modifier = modifier.height(size.spacer)
    )
}

enum class SpacerSize(val spacer: Dp) {
    XX_SMALL(Padding.SpacerXXS),
    X_SMALL(Padding.SpacerXS),
    SMALL(Padding.SpacerS),
    MEDIUM(Padding.SpacerM),
    LARGE(Padding.SpacerL),
    X_LARGE(Padding.SpacerXL)
}
