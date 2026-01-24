@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categoryManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categoryManagement.addCategory.AddCategoryScreen
import com.jie.wealthmate.feature.menu.categoryManagement.component.Category
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryTap
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.categoryManagement.modifyCategory.ModifyCategoryScreen
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class CategoryManagementScreen() : BaseScreen() {
    private val largeCategoryItems = LargeCategoryEnum.entries

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CategoryManagementScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value
        val coroutineScope = rememberCoroutineScope()
        var isDragging by remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(pageCount = { largeCategoryItems.size })
        val listState = rememberReorderableLazyListState(
            onMove = { from, to -> screenModel.handleReorderCategoryItems(from.index, to.index) }
        )

        fun onBack() {
            if (isDragging) {
                showSaveBackDialog(isDragging) {
                    isDragging = false
                    screenModel.getIncomeCategories(largeCategoryItems[pagerState.currentPage])
                }
            } else {
                navigator.pop()
            }
        }

        BackHandler(true) {
            onBack()
        }

        if (navigator.lastItem is CategoryManagementScreen) {
            SideEffect {
                if (isDragging) {
                    screenModel.updateTopBar(
                        title = TopBarItem.Title("${MenuEnum.CATEGORY.label} 순서 변경"),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { onBack() }
                        )
                    )
                } else {
                    val isAddItemEnabled = uiState.categoryItems.size < 10
                    screenModel.updateTopBar(
                        title = TopBarItem.Title(MenuEnum.CATEGORY.title),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { navigator.pop() }
                        ),
                        trailingItem = listOf(
                            TopBarItem.TrailingItem(
                                iconRes = Res.drawable.ic_add,
                                tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                                action = {
                                    if (isAddItemEnabled.not()) return@TrailingItem
                                    navigator.push(
                                        AddCategoryScreen(
                                            largeCategory = largeCategoryItems[pagerState.currentPage],
                                            categoryItems = uiState.categoryItems
                                        )
                                    )
                                }
                            )
                        )
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.getIncomeCategories(largeCategoryItems[pagerState.currentPage])
        }

        Column {
            CategoryTap(
                pagerState = pagerState,
                onTapClick = {
                    if (isDragging) {
                        screenModel.showSnackbar("순서 변경을 저장해 주세요.")
                    } else {
                        screenModel.getIncomeCategories(largeCategoryItems[it])
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(it)
                        }
                    }
                }
            )
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
                userScrollEnabled = false
            ) { _ ->

                if (uiState.categoryItems.isEmpty()) {
                    EmptyListView(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        contentText = "카테고리를 추가해주세요.",
                    )

                    return@HorizontalPager
                }

                Category(
                    listState = listState,
                    categoryItems = uiState.categoryItems,
                    isDragging = isDragging,
                    onIsDraggingChange = { isDragging = it },
                    onItemClick = {
                        goToModifyCategory(
                            navigator = navigator,
                            largeCategory = it.largeCategory,
                            categoryId = it.id
                        )
                    }
                )
            }

            // Drag and Drop 저장 버튼
            if (isDragging) {
                WMFloatingButton(
                    text = "저장",
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        screenModel.saveCategorySort()
                        isDragging = false
                    }
                )
            }
        }
    }

    private fun goToModifyCategory(
        navigator: Navigator,
        largeCategory: LargeCategoryEnum,
        categoryId: String,
    ) {
        navigator.push(
            ModifyCategoryScreen(
                largeCategory = largeCategory,
                categoryId = categoryId
            )
        )
    }

    @Composable
    @Preview(showBackground = true)
    private fun CategorySettingScreenPreview() {
        WMTheme {
            CategoryManagementScreen()
        }
    }
}