package com.jie.wealthmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.jie.wealthmate.base.BaseUiSideEffect
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.bottomNav.BottomNavigation
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.feature.asset.AssetScreen
import com.jie.wealthmate.feature.calendar.CalendarScreen
import com.jie.wealthmate.feature.home.HomeScreen
import com.jie.wealthmate.feature.menu.MenuScreen
import com.jie.wealthmate.theme.ColorGray
import org.koin.compose.koinInject

open class MainScreen : Screen {

    @Composable
    override fun Content() {
        val mainScreenModel: MainScreenModel = koinInject()
        var isBottomNaviVisible by remember { mutableStateOf(true) }
        var selectedItem by remember { mutableStateOf(BottomNavItem.Home.route) }
        val snackbarHostState = remember { SnackbarHostState() }

        mainScreenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is BaseUiSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }
        }

        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomNaviVisible,
                    enter = slideInVertically { height -> height },
                    exit = slideOutVertically { height -> height }
                ) {
                    BottomNavigation(
                        selectedItem = selectedItem,
                        onItemSelected = { selectedItem = it }
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = calculateAdjustedToastPadding(20))
                )
            },
            containerColor = ColorGray.White,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (selectedItem) {
                    BottomNavItem.Home.route -> {
                        Navigator(
                            HomeScreen(
                                innerPadding.calculateBottomPadding()
                            )
                        ) { navigator ->
                            SlideTransition(navigator)
                            LaunchedEffect(navigator.lastItem) {
                                isBottomNaviVisible = (navigator.lastItem is HomeScreen)
                            }
                        }
                    }

                    BottomNavItem.Calendar.route -> {
                        Navigator(
                            CalendarScreen(
                                innerPadding.calculateBottomPadding()
                            )
                        ) { navigator ->
                            SlideTransition(navigator)
                            LaunchedEffect(navigator.lastItem) {
                                isBottomNaviVisible = (navigator.lastItem is CalendarScreen)
                            }
                        }
                    }

                    BottomNavItem.Asset.route -> {
                        Navigator(
                            AssetScreen(
                                innerPadding.calculateBottomPadding()
                            )
                        ) { navigator ->
                            SlideTransition(navigator)
                            LaunchedEffect(navigator.lastItem) {
                                isBottomNaviVisible = (navigator.lastItem is AssetScreen)
                            }
                        }
                    }

                    BottomNavItem.Menu.route -> {
                        Navigator(
                            MenuScreen(
                                innerPadding.calculateBottomPadding()
                            )
                        ) { navigator ->
                            SlideTransition(navigator)
                            LaunchedEffect(navigator.lastItem) {
                                isBottomNaviVisible = (navigator.lastItem is MenuScreen)
                            }
                        }
                    }
                }
            }
        }
    }
}