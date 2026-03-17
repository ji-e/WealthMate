package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.CategoryTab
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryScreen
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.component.RepeatHistoryList
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.repeatHistoryDetail.RepeatHistoryDetailScreen
import com.jie.wealthmate.theme.ColorGray
import kotlinx.coroutines.launch
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class RepeatHistoryManagementScreen : BaseScreen() {
    private val largeCategoryItems = LargeCategoryEnum.entries

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: RepeatHistoryManagementScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { largeCategoryItems.size })

        val isAddItemEnabled by remember { derivedStateOf { uiState.repeatHistoryItems.size < 15 } }

        LaunchedEffect(pagerState.currentPage) {
            screenModel.changeTab(largeCategoryItems[pagerState.currentPage])
        }

        Column {
            WMTopBar(
                title = TopBarItem.Title(MenuEnum.REPEAT_HISTORY.title),
                readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_add,
                        tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                        action = {
                            if (isAddItemEnabled.not()) return@TrailingItem
                            navigator.push(AddRepeatHistoryScreen())
                        }
                    )
                )
            )

            CategoryTab(
                pagerState = pagerState,
                onTapClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(it) }
                }
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentText = "반복 내역이 없습니다.",
                    )
                } else {
                    RepeatHistoryList(
                        repeatHistoryItems = items,
                        onItemClick = {
                            navigator.push(RepeatHistoryDetailScreen(it.repeatCycle.id))
                        },
                        onIsActiveChange = screenModel::modifyRepeatCycle
                    )
                }
            }
        }
    }
}
