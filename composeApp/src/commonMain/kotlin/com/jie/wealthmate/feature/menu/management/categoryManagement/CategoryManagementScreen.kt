@file:OptIn(ExperimentalComposeUiApi::class)

package com.jie.wealthmate.feature.menu.management.categoryManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jie.wealthmate.base.BackHandlerWrapper
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.reorderable.ItemPosition
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.Category
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.CategoryTab
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

@Composable
fun CategoryManagementScreen(
    navController: NavController,
    viewModel: CategoryManagementViewModel = koinViewModel(),
) {
    BaseScreen(viewModel = viewModel) { uiState ->
        CategoryManagementContent(
            uiState = uiState,
            categoryMap = viewModel.categoryMap,
            currentCategoryItems = viewModel.currentCategoryItems,
            onBack = { navController.popBackStack() },
            onAddClick = { currentLargeCategory ->
                navController.navigate("editCategory/$currentLargeCategory/")
            },
            onItemClick = { category ->
                navController.navigate("editCategory/${category.largeCategory.name}/${category.id}")
            },
            onTabChange = viewModel::changeTab,
            onReorderCancel = viewModel::getCategories,
            onSaveSort = viewModel::saveCategorySort,
            onHandleReorder = viewModel::handleReorderCategoryItems,
            onDragOver = viewModel::onDragOver,
            onShowSnackbar = viewModel::showSnackbar
        )
    }
}

@Composable
fun CategoryManagementContent(
    uiState: CategoryManagementUiState,
    categoryMap: Map<LargeCategoryEnum, List<CategoryVo>>,
    currentCategoryItems: List<CategoryVo>?,
    onBack: () -> Unit,
    onAddClick: (String) -> Unit,
    onItemClick: (CategoryVo) -> Unit,
    onTabChange: (LargeCategoryEnum) -> Unit,
    onReorderCancel: (LargeCategoryEnum) -> Unit,
    onSaveSort: () -> Unit,
    onHandleReorder: (ItemPosition, ItemPosition) -> Unit,
    onDragOver: (ItemPosition) -> Boolean,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowSaveBackDialog by remember { mutableStateOf(false) }
    val largeCategoryItems = LargeCategoryEnum.entries
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = largeCategoryItems.indexOf(uiState.currentLargeCategory).coerceAtLeast(0),
        pageCount = { largeCategoryItems.size }
    )

    val listStates = largeCategoryItems.associateWith {
        rememberReorderableLazyListState(
            onMove = onHandleReorder,
            canDragOver = { draggedOver, _ -> onDragOver(draggedOver) },
        )
    }

    val isAddItemEnabled by remember(currentCategoryItems?.size) {
        derivedStateOf { currentCategoryItems?.size.default() < 15 }
    }

    val handleBack: () -> Unit = {
        if (isDragging) {
            isShowSaveBackDialog = true
        } else {
            onBack()
        }
    }

    BackHandlerWrapper(onBack = handleBack)

    LaunchedEffect(pagerState.currentPage) {
        onTabChange(largeCategoryItems[pagerState.currentPage])
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        CategoryManagementTopBar(
            isDragging = isDragging,
            isAddItemEnabled = isAddItemEnabled,
            onBack = handleBack,
            onAddClick = {
                val currentLargeCategory = largeCategoryItems[pagerState.currentPage].name
                onAddClick(currentLargeCategory)
            }
        )

        CategoryTab(
            pagerState = pagerState,
            onTapClick = {
                if (isDragging) {
                    onShowSnackbar("순서 변경을 저장해 주세요.")
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
            val items = categoryMap[categoryType] ?: return@HorizontalPager
            val currentListState = listStates[categoryType]!!

            if (items.isEmpty()) {
                EmptyListView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Padding.BackgroundHorizontal),
                    contentText = "카테고리를 추가해주세요.",
                )
            } else {
                Category(
                    listState = currentListState,
                    categoryItems = items,
                    isDragging = isDragging,
                    onIsDraggingChange = { isDragging = it },
                    onItemClick = onItemClick
                )
            }
        }

        if (isDragging) {
            WMFloatingButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                onClick = {
                    onSaveSort()
                    isDragging = false
                }
            )
        }

        if (isShowSaveBackDialog) {
            WMSaveBackDialog(
                onConfirm = {
                    isShowSaveBackDialog = false
                    isDragging = false
                    onReorderCancel(largeCategoryItems[pagerState.currentPage])
                },
                onDismiss = { isShowSaveBackDialog = false }
            )
        }
    }
}

@Composable
private fun CategoryManagementTopBar(
    isDragging: Boolean,
    isAddItemEnabled: Boolean,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
) {
    if (isDragging) {
        WMTopBar(
            title = TopBarItem.Title("${MenuEnum.CATEGORY.label} 순서 변경"),
            readingItem = TopBarItem.ReadingItem(action = onBack)
        )
    } else {
        WMTopBar(
            title = TopBarItem.Title(MenuEnum.CATEGORY.title),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_add,
                    tint = if (isAddItemEnabled) ColorSetting.Default else ColorSetting.DisabledBackground,
                    action = { if (isAddItemEnabled) onAddClick() }
                )
            )
        )
    }
}

@Preview
@Composable
private fun CategoryManagementContentPreview() {
    WMTheme {
        CategoryManagementContent(
            uiState = CategoryManagementUiState(),
            categoryMap = emptyMap(),
            currentCategoryItems = emptyList(),
            onBack = {},
            onAddClick = {},
            onItemClick = {},
            onTabChange = {},
            onReorderCancel = {},
            onSaveSort = {},
            onHandleReorder = { _, _ -> },
            onDragOver = { false },
            onShowSnackbar = {}
        )
    }
}
