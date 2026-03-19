package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme

/**
 * WealthMate 공통 구분선 (그림자 포함)
 * 주로 하단 버튼 영역과 리스트 영역을 시각적으로 분리할 때 사용합니다.
 */
@Composable
fun WMShadowDivider(
    modifier: Modifier = Modifier
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = ColorGray.Gray_600.copy(alpha = 0.5f),
                ambientColor = ColorSetting.DisabledBackground.copy(alpha = 0.2f)
            )
    )
}

/**
 * WealthMate 공통 기본 가로 구분선
 */
@Composable
fun WMHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = ColorSetting.DisabledBackground
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
private fun DividersPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText(text = "Standard Horizontal Divider", fontWeight = FontWeight.Bold)
                WMHorizontalDivider()
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText(text = "Shadow Divider", fontWeight = FontWeight.Bold)
                // 그림자 효과를 확인하기 위해 아래에 공간을 둡니다.
                WMShadowDivider()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
