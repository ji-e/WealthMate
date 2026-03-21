package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.CategoryTab
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.component.RepeatHistoryList
import com.jie.wealthmate.theme.ColorGray
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

@Composable
fun RepeatHistoryManagementScreen(
    navController: NavController,
    initialLargeCategory: LargeCategoryEnum = LargeCategoryEnum.INCOME,
    viewModel: RepeatHistoryManagementViewModel = koinViewModel(),
) {
    val largeCategoryItems = LargeCategoryEnum.entries
    val uiState by viewModel.container.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val initialPage = remember { largeCategoryItems.indexOf(initialLargeCategory).coerceAtLeast(0) }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { largeCategoryItems.size }
    )

    val isAddItemEnabled by remember { derivedStateOf { uiState.repeatHistoryItems.size < 15 } }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.changeTab(largeCategoryItems[pagerState.currentPage])
    }

    BaseScreen(viewModel = viewModel) {
        RepeatHistoryManagementContent(
            uiState = uiState,
            pagerState = pagerState,
            largeCategoryItems = largeCategoryItems,
            isAddItemEnabled = isAddItemEnabled,
            onBack = { navController.popBackStack() },
            onAddClick = {
                val currentLargeCategory = largeCategoryItems[pagerState.currentPage].name
                navController.navigate("addRepeatHistory/$currentLargeCategory")
            },
            onTabClick = { coroutineScope.launch { pagerState.animateScrollToPage(it) } },
            onItemClick = { id -> navController.navigate("repeatHistoryDetail/$id") },
            onIsActiveChange = viewModel::modifyRepeatCycle
        )
    }
}

@Composable
fun RepeatHistoryManagementContent(
    uiState: RepeatHistoryManagementUiState,
    pagerState: androidx.compose.foundation.pager.PagerState,
    largeCategoryItems: List<LargeCategoryEnum>,
    isAddItemEnabled: Boolean,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onTabClick: (Int) -> Unit,
    onItemClick: (String) -> Unit,
    onIsActiveChange: (RepeatCycleEntity, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth()
    ) {
        WMTopBar(
            title = TopBarItem.Title(MenuEnum.REPEAT_HISTORY.title),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_add,
                    tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                    action = { if (isAddItemEnabled) onAddClick() }
                )
            )
        )

        CategoryTab(
            pagerState = pagerState,
            onTapClick = onTabClick
        )

        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            userScrollEnabled = false,
            beyondViewportPageCount = 1
        ) { pageIndex ->
            val items = uiState.repeatHistoryItems.filter {
                it.category?.largeCategory == largeCategoryItems[pageIndex].name
            }

            if (items.isEmpty()) {
                EmptyListView(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    contentText = "반복 내역이 없습니다.",
                )
            } else {
                RepeatHistoryList(
                    repeatHistoryItems = items,
                    onItemClick = { onItemClick(it.repeatCycle.id) },
                    onIsActiveChange = onIsActiveChange
                )
            }
        }
    }
}
