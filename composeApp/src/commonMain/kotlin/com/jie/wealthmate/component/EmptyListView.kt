package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jie.wealthmate.theme.ColorGray

@Composable
fun EmptyListView(
    modifier: Modifier = Modifier,
    contentText: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = contentText,
            style = Typography().bodyLarge.copy(color = ColorGray.Gray_400)
        )
    }
}
