package com.jie.wealthmate.component.bottomNav

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.resources.painterResource

private val NavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Calendar,
    BottomNavItem.Add,
    BottomNavItem.Budget,
    BottomNavItem.Menu
)

/**
 * 앱의 메인 하단 내비게이션 바 컴포넌트
 */
@Composable
fun BottomNavigation(
    selectedItem: String,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit = { },
) {
    val shape = remember { RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ColorGray.White)
            .border(
                width = 0.5.dp,
                color = ColorSetting.DisabledBackground,
                shape = shape
            )
    ) {
        NavigationBar(
            containerColor = ColorGray.White,
            tonalElevation = 0.dp // 커스텀 배경색과 테두리를 유지하기 위해 토널 엘리베이션 제거
        ) {
            NavItems.forEach { item ->
                val isSelected = selectedItem == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(item.route) },
                    icon = {
                        Icon(
                            painter = painterResource(item.icon),
                            modifier = Modifier.size(24.dp),
                            contentDescription = item.label
                        )
                    },
                    label = {
                        WMText(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) ColorSetting.Default else ColorSetting.DisabledContent
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ColorSetting.Default,
                        unselectedIconColor = ColorSetting.DisabledContent,
                        selectedTextColor = ColorSetting.Default,
                        unselectedTextColor = ColorSetting.DisabledContent,
                        indicatorColor = ColorPrimary.Primary_200
                    )
                )
            }
        }
    }
}

@Composable
@Preview
private fun BottomNavigationPreview() {
    WMTheme {
        BottomNavigation(
            selectedItem = BottomNavItem.Home.route,
            onItemSelected = {}
        )
    }
}
