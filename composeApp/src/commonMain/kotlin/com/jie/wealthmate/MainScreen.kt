package com.jie.wealthmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.jie.wealthmate.base.BaseUiSideEffect
import com.jie.wealthmate.component.CustomSnackbarHost
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.bottomNav.BottomNavigation
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.rememberSnackbarState
import com.jie.wealthmate.feature.asset.AssetScreen
import com.jie.wealthmate.feature.calendar.CalendarScreen
import com.jie.wealthmate.feature.home.HomeScreen
import com.jie.wealthmate.feature.menu.MenuScreen
import com.jie.wealthmate.theme.ColorGray
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.launch
import wealthmate.composeapp.generated.resources.Res

open class MainScreen : Screen {

    @Composable
    override fun Content() {
        val uiState by MainUiManager.uiState.collectAsState()

        var isShowLoading by remember { mutableStateOf(false) }
        var isBottomNaviVisible by remember { mutableStateOf(true) }
        var selectedItem by remember { mutableStateOf(BottomNavItem.Home.route) }
        val snackbarState = rememberSnackbarState()
        val scope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            MainUiManager.sideEffect.collect { sideEffect ->
                when (sideEffect) {
                    is BaseUiSideEffect.ShowLoading -> {
                        isShowLoading = sideEffect.isShowLoading
                    }

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
                }
            }
        }

        Box {
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
                contentWindowInsets = WindowInsets.systemBars,
                containerColor = ColorGray.White,
            ) { innerPadding ->
                Column(
                    modifier = Modifier
//                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (selectedItem) {
                        BottomNavItem.Home.route -> {
                            Navigator(HomeScreen(innerPadding.calculateBottomPadding())) { navigator ->
//                                SlideTransition(navigator)
                                CurrentScreen()
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is HomeScreen)
                                }
                            }
                        }

                        BottomNavItem.Calendar.route -> {
                            Navigator(CalendarScreen()) { navigator ->
//                                ScaleTransition(navigator)
                                CurrentScreen()
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is CalendarScreen)
                                }
                            }
                        }

                        BottomNavItem.Asset.route -> {
                            Navigator(AssetScreen(innerPadding.calculateBottomPadding())) { navigator ->
//                                ScaleTransition(navigator)
                                CurrentScreen()
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is AssetScreen)
                                }
                            }
                        }

                        BottomNavItem.Menu.route -> {
                            Navigator(MenuScreen(innerPadding.calculateBottomPadding())) { navigator ->
                                CurrentScreen()
                                LaunchedEffect(navigator.lastItem) {
                                    isBottomNaviVisible = (navigator.lastItem is MenuScreen)
                                }
                            }
                        }
                    }
                }
            }

            Loading(isShowLoading)

            CustomSnackbarHost(
                snackbarState = snackbarState,
                modifier = Modifier.padding(bottom = calculateAdjustedToastPadding(60))
            )
        }
    }

    @Composable
    private fun Loading(isShowLoading: Boolean) {
        if (isShowLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(false) {},
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition {
                    val animationBytes = Res.readBytes("files/loading_animation.json")
                    LottieCompositionSpec.JsonString(animationBytes.decodeToString())
                }

                Image(
                    contentDescription = "Lottie animation",
                    painter = rememberLottiePainter(
                        composition = composition,
                        iterations = Compottie.IterateForever
                    )
                )
            }
        }
    }
}
