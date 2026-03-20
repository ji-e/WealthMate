package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme

/**
 * 리스트에 표시할 데이터가 없을 때 사용되는 공통 뷰
 */
@Composable
fun EmptyListView(
    modifier: Modifier = Modifier.fillMaxSize(),
    contentText: String,
) {
    Box(
        modifier = modifier.padding(
            vertical = Padding.SpacerL,
            horizontal = Padding.ContainerHorizontal
        ),
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = contentText,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorSetting.Empty,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyListViewPreview() {
    WMTheme {
        EmptyListView(
            contentText = "표시할 내역이 없습니다."
        )
    }
}
