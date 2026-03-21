@file:OptIn(ExperimentalComposeUiApi::class)

package com.jie.wealthmate.feature.menu.management.categoryManagement

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.Category
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.CategoryTab
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

@Composable
fun CategoryManagementScreen(
    navController: NavController,
    viewModel: CategoryManagementViewModel = koinViewModel(),
) {
    val largeCategoryItems = LargeCategoryEnum.entries
    val uiState by viewModel.container.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { largeCategoryItems.size })

    val listStates = largeCategoryItems.associateWith {
        rememberReorderableLazyListState(
            onMove = { from, to ->
                viewModel.handleReorderCategoryItems(from.index, to.index)
            }
        )
    }

    val isAddItemEnabled by remember {
        derivedStateOf { uiState.currentCategoryItems?.size.default() < 15 }
    }

    val onBack: () -> Unit = {
        if (isDragging) {
            // TODO: Use viewModel to show confirmation or handle local state
            isDragging = false
            viewModel.getCategories(largeCategoryItems[pagerState.currentPage])
        } else {
            navController.popBackStack()
        }
    }

    BackHandler(enabled = true, onBack = onBack)

    LaunchedEffect(pagerState.currentPage) {
        viewModel.changeTab(largeCategoryItems[pagerState.currentPage])
    }

    BaseScreen(viewModel = viewModel) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            if (isDragging) {
                WMTopBar(
                    title = TopBarItem.Title("${MenuEnum.CATEGORY.label} 순서 변경"),
                    readingItem = TopBarItem.ReadingItem(action = onBack)
                )
            } else {
                WMTopBar(
                    title = TopBarItem.Title(MenuEnum.CATEGORY.title),
                    readingItem = TopBarItem.ReadingItem(action = { navController.popBackStack() }),
                    trailingItem = listOf(
                        TopBarItem.TrailingItem(
                            iconRes = Res.drawable.ic_add,
                            tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                            action = {
                                if (isAddItemEnabled) {
                                    val currentLargeCategory =
                                        largeCategoryItems[pagerState.currentPage].name
                                    navController.navigate("addCategory/$currentLargeCategory")
                                }
                            }
                        )
                    )
                )
            }

            CategoryTab(
                pagerState = pagerState,
                onTapClick = {
                    if (isDragging) {
                        viewModel.showSnackbar("순서 변경을 저장해 주세요.")
                    } else {
                        coroutineScope.launch { pagerState.animateScrollToPage(it) }
                    }
                }
            )

            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
                userScrollEnabled = false,
                beyondViewportPageCount = 1
            ) { pageIndex ->
                val categoryType = largeCategoryItems[pageIndex]
                val items = uiState.categoryMap[categoryType] ?: return@HorizontalPager
                val currentListState = listStates[categoryType]!!

                if (items.isEmpty()) {
                    EmptyListView(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        contentText = "카테고리를 추가해주세요.",
                    )
                } else {
                    Category(
                        listState = currentListState,
                        categoryItems = items,
                        isDragging = isDragging,
                        onIsDraggingChange = { isDragging = it },
                        onItemClick = { category ->
                            navController.navigate("modifyCategory/${category.largeCategory.name}/${category.id}")
                        }
                    )
                }
            }

            if (isDragging) {
                WMFloatingButton(
                    text = "저장",
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        viewModel.saveCategorySort()
                        isDragging = false
                    }
                )
            }
        }
    }
}
