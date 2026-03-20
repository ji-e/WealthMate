package com.jie.wealthmate.feature.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.component.CustomSnackbarHost
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.bottomNav.BottomNavigation
import com.jie.wealthmate.component.rememberSnackbarState
import com.jie.wealthmate.feature.budget.BudgetScreen
import com.jie.wealthmate.feature.calendar.CalendarScreen
import com.jie.wealthmate.feature.home.HomeScreen
import com.jie.wealthmate.feature.menu.MenuScreen
import com.jie.wealthmate.getPlatform
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@OptIn(InternalVoyagerApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = koinViewModel(),
) {
    val uiState by viewModel.container.uiState.collectAsState()
    var isBottomBarVisible by remember { mutableStateOf(true) }
    val snackbarState = rememberSnackbarState()
    val scope = rememberCoroutineScope()
    var lastBackPressedTime by remember { mutableStateOf(0L) }
    val platform = remember { getPlatform() }

    BackHandler(enabled = true) {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        if (currentTime - lastBackPressedTime < 2000) {
            platform.exitApp()
        } else {
            lastBackPressedTime = currentTime
            scope.launch {
                snackbarState.showSnackbar("뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { _ ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (uiState.selectedItem) {
                    BottomNavItem.Home -> {
                        HomeScreen(
                            navController = navController,
//                            onShowBottomBar = { isBottomBarVisible = it }
                        )
                    }

                    BottomNavItem.Calendar -> {
                        CalendarScreen(

                        )

                    }

                    BottomNavItem.Add -> {


                    }

                    BottomNavItem.Budget -> {
                        BudgetScreen()
                    }

                    BottomNavItem.Menu -> {
                        MenuScreen()

                    }
                }
            }
        }

        // BottomNavigation을 Scaffold 외부의 Box Overlay로 배치하여 컨텐츠 크기 변화를 방지합니다.
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = isBottomBarVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            BottomNavigation(
                selectedItem = uiState.selectedItem.route,
                onItemSelected = { route ->
                    val item = when (route) {
                        BottomNavItem.Home.route -> BottomNavItem.Home
                        BottomNavItem.Calendar.route -> BottomNavItem.Calendar
                        BottomNavItem.Budget.route -> BottomNavItem.Budget
                        BottomNavItem.Menu.route -> BottomNavItem.Menu
                        else -> BottomNavItem.Home
                    }
                    viewModel.onTabSelected(item)
                }
            )
        }

        CustomSnackbarHost(
            snackbarState = snackbarState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}
