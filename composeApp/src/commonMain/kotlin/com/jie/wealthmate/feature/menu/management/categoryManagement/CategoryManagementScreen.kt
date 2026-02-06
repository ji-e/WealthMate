@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.management.categoryManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTobBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.AddCategoryScreen
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.Category
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.CategoryTab
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.ModifyCategoryScreen
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class CategoryManagementScreen : BaseScreen() {
    private val largeCategoryItems = LargeCategoryEnum.entries

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CategoryManagementScreenModel = koinInject()
        val uiState by screenModel.container.uiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        var isDragging by remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(pageCount = { largeCategoryItems.size })

        // 각 탭별로 독립적인 스크롤/Reorder 상태를 유지하기 위한 맵
        val listStates = largeCategoryItems.associateWith {
            rememberReorderableLazyListState(
                onMove = { from, to ->
                    screenModel.handleReorderCategoryItems(
                        from.index,
                        to.index
                    )
                }
            )
        }

        val isAddItemEnabled by remember {
            derivedStateOf { uiState.currentCategoryItems?.size.default() < 15 }
        }

        val onBack: () -> Unit = remember(isDragging) {
            {
                if (isDragging) {
                    showSaveBackDialog(isDragging) {
                        isDragging = false
                        screenModel.getCategories(largeCategoryItems[pagerState.currentPage])
                    }
                } else {
                    navigator.pop()
                }
            }
        }

        BackHandler(
            enabled = true,
            onBack = onBack
        )

        LaunchedEffect(pagerState.currentPage) {
            screenModel.changeTab(largeCategoryItems[pagerState.currentPage])
        }

        Column {
            if (isDragging) {
                WMTobBar(
                    title = TopBarItem.Title("${MenuEnum.CATEGORY.label} 순서 변경"),
                    readingItem = TopBarItem.ReadingItem().copy(action = { onBack() })
                )
            } else {
                WMTobBar(
                    title = TopBarItem.Title(MenuEnum.CATEGORY.title),
                    readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
                    trailingItem = listOf(
                        TopBarItem.TrailingItem(
                            iconRes = Res.drawable.ic_add,
                            tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                            action = {
                                if (isAddItemEnabled.not()) return@TrailingItem
                                navigator.push(AddCategoryScreen(largeCategory = largeCategoryItems[pagerState.currentPage]))
                            }
                        )
                    )
                )
            }

            CategoryTab(
                pagerState = pagerState,
                onTapClick = {
                    if (isDragging) {
                        screenModel.showSnackbar("순서 변경을 저장해 주세요.")
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
                        onItemClick = {
                            navigator.push(
                                ModifyCategoryScreen(
                                    largeCategory = it.largeCategory,
                                    categoryId = it.id
                                )
                            )
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
                        screenModel.saveCategorySort()
                        isDragging = false
                    }
                )
            }
        }
    }
}
