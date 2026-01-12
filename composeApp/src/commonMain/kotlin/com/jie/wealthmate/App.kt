package com.jie.wealthmate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.bottomNav.BottomNavigation
import com.jie.wealthmate.feature.asset.AssetScreen
import com.jie.wealthmate.feature.calendar.CalendarScreen
import com.jie.wealthmate.feature.home.HomeScreen
import com.jie.wealthmate.feature.menu.MenuScreen
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    WMTheme() {
        var selectedItem by remember { mutableStateOf(BottomNavItem.Home.route) }

        Scaffold(
            bottomBar = {
                BottomNavigation(
                    selectedItem = selectedItem,
                    onItemSelected = { selectedItem = it }
                )
            },
            containerColor = ColorGray.White,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (selectedItem) {
                    BottomNavItem.Home.route -> {
                        Navigator(HomeScreen()) { navigator ->
                            SlideTransition(navigator)
                        }
                    }

                    BottomNavItem.Calendar.route -> {
                        Navigator(CalendarScreen()) { navigator ->
                            SlideTransition(navigator)
                        }
                    }

                    BottomNavItem.Asset.route -> {
                        Navigator(AssetScreen()) { navigator ->
                            SlideTransition(navigator)
                        }
                    }

                    BottomNavItem.Menu.route -> {
                        Navigator(MenuScreen()) { navigator ->
                            SlideTransition(navigator)
                        }
                    }
                }
            }
        }
    }
}
