package com.jie.wealthmate.feature.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.MainUiManager
import com.jie.wealthmate.component.CustomSnackbarHost
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.bottomNav.BottomNavigation
import com.jie.wealthmate.component.rememberSnackbarState
import com.jie.wealthmate.feature.budget.BudgetScreen
import com.jie.wealthmate.feature.budget.addBudget.AddBudgetScreen
import com.jie.wealthmate.feature.budget.budgetDetail.BudgetDetailScreen
import com.jie.wealthmate.feature.budget.budgetSetting.BudgetSettingScreen
import com.jie.wealthmate.feature.budget.budgetYearDetail.BudgetYearDetailScreen
import com.jie.wealthmate.feature.calendar.CalendarScreen
import com.jie.wealthmate.feature.calendar.addHistory.AddHistoryScreen
import com.jie.wealthmate.feature.calendar.historyDetail.HistoryDetailScreen
import com.jie.wealthmate.feature.home.HomeScreen
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.categoryExpenses.CategoryExpensesScreen
import com.jie.wealthmate.feature.home.paymentMethodExpenses.PaymentMethodExpensesScreen
import com.jie.wealthmate.feature.home.preparednessStatus.PreparednessStatusScreen
import com.jie.wealthmate.feature.menu.MenuScreen
import com.jie.wealthmate.feature.menu.data.googleCloudShare.GoogleCloudShareScreen
import com.jie.wealthmate.feature.menu.data.googleCloudSync.GoogleCloudSyncScreen
import com.jie.wealthmate.feature.menu.management.categoryManagement.CategoryManagementScreen
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.AddCategoryScreen
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.ModifyCategoryScreen
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.PaymentMethodManagementScreen
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.AddPaymentMethodScreen
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod.ModifyPaymentMethodScreen
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.RepeatHistoryManagementScreen
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryScreen
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.repeatHistoryDetail.RepeatHistoryDetailScreen
import com.jie.wealthmate.feature.search.SearchScreen
import com.jie.wealthmate.getPlatform
import com.jie.wealthmate.utils.convertDateToLocalDate
import com.jie.wealthmate.utils.today
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(InternalVoyagerApi::class, ExperimentalTime::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = koinViewModel(),
) {
    val uiState by viewModel.container.uiState.collectAsState()
    val mainUiState by MainUiManager.uiState.collectAsState()
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible by remember(currentRoute) {
        derivedStateOf {
            when (currentRoute) {
                BottomNavItem.Home.route,
                BottomNavItem.Calendar.route,
                BottomNavItem.Budget.route,
                BottomNavItem.Menu.route,
                    -> true

                else -> false
            }
        }
    }
    val snackbarState = rememberSnackbarState()
    val scope = rememberCoroutineScope()
    var lastBackPressedTime by remember { mutableStateOf(0L) }
    val platform = remember { getPlatform() }

    BackHandler(enabled = true) {
        if (uiState.selectedItem == BottomNavItem.Add) {
            viewModel.navigateBackToPreviousTab()
        } else if (innerNavController.previousBackStackEntry != null) {
            innerNavController.popBackStack()
        } else {
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
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { _ ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                NavHost(
                    navController = innerNavController,
                    startDestination = BottomNavItem.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(BottomNavItem.Home.route) {
                        HomeScreen(navController = innerNavController)
                    }
                    composable(BottomNavItem.Calendar.route) {
                        CalendarScreen(navController = innerNavController)
                    }
                    composable(BottomNavItem.Add.route) {
                        AddHistoryScreen(
                            navController = innerNavController,
                            initialSelectedDate = mainUiState.selectedDate,
                            onBack = { viewModel.navigateBackToPreviousTab() }
                        )
                    }
                    composable(BottomNavItem.Budget.route) {
                        BudgetScreen(navController = innerNavController)
                    }
                    composable(BottomNavItem.Menu.route) {
                        MenuScreen(navController = innerNavController)
                    }
                    composable("search") {
                        SearchScreen(navController = innerNavController)
                    }
                    composable(
                        route = "historyDetail/{largeCategory}/{historyId}",
                        arguments = listOf(
                            navArgument("largeCategory") { type = NavType.StringType },
                            navArgument("historyId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val historyId: String = backStackEntry.savedStateHandle["historyId"] ?: ""
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }

                        HistoryDetailScreen(
                            largeCategory = largeCategory,
                            historyId = historyId,
                            onBack = { innerNavController.popBackStack() },
                            onNavigateToRepeatDetail = { id ->
                                innerNavController.navigate("repeatHistoryDetail/$id")
                            }
                        )
                    }
                    composable(
                        route = "preparednessStatus/{statusType}/{largeCategory}?scrollToPosition={scrollToPosition}",
                        arguments = listOf(
                            navArgument("statusType") { type = NavType.StringType },
                            navArgument("largeCategory") { type = NavType.StringType },
                            navArgument("scrollToPosition") {
                                type = NavType.IntType
                                defaultValue = 0
                            }
                        )
                    ) { backStackEntry ->
                        val statusTypeStr: String? = backStackEntry.savedStateHandle["statusType"]
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val scrollToPosition: Int = backStackEntry.savedStateHandle["scrollToPosition"] ?: 0

                        val statusType = try {
                            StatusType.valueOf(statusTypeStr ?: "MONTH")
                        } catch (e: Exception) {
                            StatusType.MONTH
                        }
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }

                        PreparednessStatusScreen(
                            navController = innerNavController,
                            initialStatusType = statusType,
                            initialLargeCategory = largeCategory,
                            scrollToPosition = scrollToPosition
                        )
                    }
                    composable(
                        route = "categoryExpenses/{statusType}/{largeCategory}/{categoryId}",
                        arguments = listOf(
                            navArgument("statusType") { type = NavType.StringType },
                            navArgument("largeCategory") { type = NavType.StringType },
                            navArgument("categoryId") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ) { backStackEntry ->
                        val statusTypeStr: String? = backStackEntry.savedStateHandle["statusType"]
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val categoryId: String? = backStackEntry.savedStateHandle["categoryId"]

                        val statusType = try {
                            StatusType.valueOf(statusTypeStr ?: "MONTH")
                        } catch (e: Exception) {
                            StatusType.MONTH
                        }
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }

                        CategoryExpensesScreen(
                            navController = innerNavController,
                            initialStatusType = statusType,
                            initialLargeCategory = largeCategory,
                            categoryId = categoryId
                        )
                    }
                    composable(
                        route = "paymentMethodExpenses/{statusType}/{largeCategory}/{paymentMethodId}",
                        arguments = listOf(
                            navArgument("statusType") { type = NavType.StringType },
                            navArgument("largeCategory") { type = NavType.StringType },
                            navArgument("paymentMethodId") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ) { backStackEntry ->
                        val statusTypeStr: String? = backStackEntry.savedStateHandle["statusType"]
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val paymentMethodId: String? = backStackEntry.savedStateHandle["paymentMethodId"]

                        val statusType = try {
                            StatusType.valueOf(statusTypeStr ?: "MONTH")
                        } catch (e: Exception) {
                            StatusType.MONTH
                        }
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }

                        PaymentMethodExpensesScreen(
                            navController = innerNavController,
                            initialStatusType = statusType,
                            initialLargeCategory = largeCategory,
                            paymentMethodId = paymentMethodId
                        )
                    }

                    // Category Management Routes
                    composable("categoryManagement") {
                        CategoryManagementScreen(navController = innerNavController)
                    }
                    composable(
                        route = "addCategory/{largeCategory}",
                        arguments = listOf(
                            navArgument("largeCategory") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }
                        AddCategoryScreen(
                            navController = innerNavController,
                            largeCategory = largeCategory
                        )
                    }
                    composable(
                        route = "modifyCategory/{largeCategory}/{categoryId}",
                        arguments = listOf(
                            navArgument("largeCategory") { type = NavType.StringType },
                            navArgument("categoryId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val categoryId: String = backStackEntry.savedStateHandle["categoryId"] ?: ""
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }
                        ModifyCategoryScreen(
                            navController = innerNavController,
                            largeCategory = largeCategory,
                            categoryId = categoryId
                        )
                    }

                    // Payment Method Management Routes
                    composable("paymentMethodManagement") {
                        PaymentMethodManagementScreen(navController = innerNavController)
                    }
                    composable("addPaymentMethod") {
                        AddPaymentMethodScreen(navController = innerNavController)
                    }
                    composable(
                        route = "modifyPaymentMethod/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val id: String = backStackEntry.savedStateHandle["id"] ?: ""
                        ModifyPaymentMethodScreen(navController = innerNavController, paymentMethodId = id)
                    }

                    // Repeat History Management Routes
                    composable(
                        route = "repeatHistoryManagement/{largeCategory}",
                        arguments = listOf(
                            navArgument("largeCategory") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }

                        RepeatHistoryManagementScreen(
                            navController = innerNavController,
                            initialLargeCategory = largeCategory
                        )
                    }
                    composable(
                        route = "repeatHistoryDetail/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val id: String = backStackEntry.savedStateHandle["id"] ?: ""
                        RepeatHistoryDetailScreen(
                            navController = innerNavController,
                            repeatCycleId = id
                        )
                    }
                    composable(
                        route = "addRepeatHistory/{largeCategory}",
                        arguments = listOf(
                            navArgument("largeCategory") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val largeCategoryStr: String? = backStackEntry.savedStateHandle["largeCategory"]
                        val largeCategory = try {
                            LargeCategoryEnum.valueOf(largeCategoryStr ?: "EXPENSES")
                        } catch (e: Exception) {
                            LargeCategoryEnum.EXPENSES
                        }
                        AddRepeatHistoryScreen(
                            navController = innerNavController,
                            largeCategory = largeCategory
                        )
                    }

                    // Data Sync Routes
                    composable("googleCloudSync") {
                        GoogleCloudSyncScreen(navController = innerNavController)
                    }
                    composable("googleCloudShare") {
                        GoogleCloudShareScreen(navController = innerNavController)
                    }

                    // Budget Routes
                    composable("budgetSetting") {
                        BudgetSettingScreen(navController = innerNavController)
                    }
                    composable(
                        route = "budgetDetail/{selectedMonth}",
                        arguments = listOf(
                            navArgument("selectedMonth") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val selectedMonthStr: String? = backStackEntry.savedStateHandle["selectedMonth"]
                        val selectedMonth = selectedMonthStr?.convertDateToLocalDate() ?: today
                        BudgetDetailScreen(
                            navController = innerNavController,
                            selectedMonth = selectedMonth
                        )
                    }
                    composable(
                        route = "budgetYearDetail/{selectedYear}",
                        arguments = listOf(
                            navArgument("selectedYear") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val selectedYear: String = backStackEntry.savedStateHandle["selectedYear"] ?: today.year.toString()
                        BudgetYearDetailScreen(
                            navController = innerNavController,
                            selectedYear = selectedYear
                        )
                    }
                    composable(
                        route = "addBudget?selectedYearMonth={selectedYearMonth}&isEditMode={isEditMode}&isCopyMode={isCopyMode}",
                        arguments = listOf(
                            navArgument("selectedYearMonth") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            },
                            navArgument("isEditMode") {
                                type = NavType.BoolType
                                defaultValue = false
                            },
                            navArgument("isCopyMode") {
                                type = NavType.BoolType
                                defaultValue = false
                            }
                        )
                    ) { backStackEntry ->
                        val selectedYearMonth: String? = backStackEntry.savedStateHandle["selectedYearMonth"]
                        val isEditMode: Boolean = backStackEntry.savedStateHandle["isEditMode"] ?: false
                        val isCopyMode: Boolean = backStackEntry.savedStateHandle["isCopyMode"] ?: false
                        AddBudgetScreen(
                            navController = innerNavController,
                            selectedYearMonth = selectedYearMonth,
                            isEditMode = isEditMode,
                            isCopyMode = isCopyMode
                        )
                    }
                }

                // Sync tab selection with NavHost
                LaunchedEffect(uiState.selectedItem) {
                    val currentRoute = innerNavController.currentDestination?.route
                    if (currentRoute != uiState.selectedItem.route) {
                        innerNavController.navigate(uiState.selectedItem.route) {
                            popUpTo(innerNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                        BottomNavItem.Add.route -> BottomNavItem.Add
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
