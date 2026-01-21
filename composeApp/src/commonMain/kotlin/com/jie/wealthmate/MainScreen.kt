package com.jie.wealthmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.jie.wealthmate.base.BaseUiSideEffect
import com.jie.wealthmate.component.CustomSnackbarHost
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.bottomNav.BottomNavigation
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.rememberSnackbarState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTobBar
import com.jie.wealthmate.feature.asset.AssetScreen
import com.jie.wealthmate.feature.calendar.CalendarScreen
import com.jie.wealthmate.feature.home.HomeScreen
import com.jie.wealthmate.feature.menu.MenuScreen
import com.jie.wealthmate.theme.ColorGray
import kotlinx.coroutines.launch

open class MainScreen : Screen {

    @Composable
    override fun Content() {
        val uiState by MainUiManager.uiState.collectAsState()

        var isBottomNaviVisible by remember { mutableStateOf(true) }
        var selectedItem by remember { mutableStateOf(BottomNavItem.Home.route) }
        val snackbarState = rememberSnackbarState()
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            MainUiManager.sideEffect.collect { sideEffect ->
                when (sideEffect) {
                    is BaseUiSideEffect.ShowSnackbar -> {
                        scope.launch {
                            snackbarState.showSnackbar(message = sideEffect.message)
                        }
                    }

                    is BaseUiSideEffect.ShowSnackbarWithAction -> {
                        scope.launch {
                            snackbarState.showSnackbar(
                                message = sideEffect.message,
                                actionLabel = sideEffect.actionLabel,
                                onAction = sideEffect.onAction
                            )
                        }
                    }

                    is BaseUiSideEffect.HideKeyboard -> {
                        focusManager.clearFocus(true)
                    }

                    else -> Unit
                }
            }
        }

        Box {
            Scaffold(
                topBar = {
                    WMTobBar(
                        title = uiState.title ?: TopBarItem.Title(""),
                        readingItem = uiState.readingItem,
                        trailingItem = uiState.trailingItem
                    )
                },
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
                containerColor = ColorGray.White,
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .fillMaxSize()
                        .padding(
                            top = 64.dp,
//                            bottom = if (isBottomNaviVisible) innerPadding.calculateBottomPadding() else 4.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (selectedItem) {
                        BottomNavItem.Home.route -> {
                            Navigator(HomeScreen(innerPadding.calculateBottomPadding())) { navigator ->
                                SlideTransition(navigator)
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is HomeScreen)
                                }
                            }
                        }

                        BottomNavItem.Calendar.route -> {
                            Navigator(CalendarScreen(innerPadding.calculateBottomPadding())) { navigator ->
                                SlideTransition(navigator)
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is CalendarScreen)
                                }
                            }
                        }

                        BottomNavItem.Asset.route -> {
                            Navigator(AssetScreen(innerPadding.calculateBottomPadding())) { navigator ->
                                SlideTransition(navigator)
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is AssetScreen)
                                }
                            }
                        }

                        BottomNavItem.Menu.route -> {
                            Navigator(MenuScreen(innerPadding.calculateBottomPadding())) { navigator ->
                                SlideTransition(navigator)
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is MenuScreen)
                                }
                            }
                        }
                    }
                }
            }

            CustomSnackbarHost(
                snackbarState = snackbarState,
                modifier = Modifier.padding(bottom = calculateAdjustedToastPadding(20))
            )
        }
    }
}
