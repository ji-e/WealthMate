package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray

@Composable
fun WMShadowDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = ColorGray.Gray_600.copy(alpha = 0.5f),
                ambientColor = ColorGray.Gray_100.copy(alpha = 0.2f)
            )
    )
}