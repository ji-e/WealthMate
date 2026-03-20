package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme

/**
 * 표시할 데이터가 없을 때 사용되는 공통 뷰
 */
@Composable
fun EmptyBoxView(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentText: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(color = ColorSetting.EmptyBackground, shape = Shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = contentText,
            style = MaterialTheme.typography.bodySmall,
            color = ColorSetting.EmptyContent
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyBoxViewPreview() {
    WMTheme {
        EmptyBoxView(
            contentText = "표시할 내역이 없습니다."
        )
    }
}
