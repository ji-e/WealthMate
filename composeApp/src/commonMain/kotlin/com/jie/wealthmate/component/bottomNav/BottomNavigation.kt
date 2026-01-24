package com.jie.wealthmate.component.bottomNav

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavigation(
    selectedItem: String,
    onItemSelected: (String) -> Unit = { },
) {
    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calendar,
//        BottomNavItem.Asset,
        BottomNavItem.Menu
    )
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(ColorGray.White)
            .border(
                width = 0.5.dp,
                color = ColorGray.Gray_100,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
    ) {
        NavigationBar(
            containerColor = ColorGray.White,
        ) {
            navItems.forEach { item ->
                val isSelected = selectedItem == item.route
                NavigationBarItem(
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
                            style = Typography().bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) ColorGray.Gray_700 else ColorGray.Gray_300
                            )
                        )
                    },
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors().copy(
                        selectedIconColor = ColorGray.Gray_700,
                        selectedTextColor = ColorGray.Gray_700,
                        selectedIndicatorColor = ColorPrimary.Primary_300,
                        unselectedIconColor = ColorGray.Gray_300,
                        unselectedTextColor = ColorGray.Gray_300,
                    ),
                    onClick = { onItemSelected(item.route) },
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun BottomNavigationPreview() {
    BottomNavigation(selectedItem = BottomNavItem.Home.route)
}


