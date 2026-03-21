package com.jie.wealthmate.feature.home.paymentMethodExpenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.categoryExpenses.component.CategoryExpensesItem
import com.jie.wealthmate.feature.home.paymentMethodExpenses.component.PaymentMethodExpensesHeader
import com.jie.wealthmate.feature.home.preparednessStatus.component.PreparednessStatusFilter
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.search.component.DateHeader
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.formatDateKorMDE
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.PaymentMethodVo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PaymentMethodExpensesScreen(
    navController: NavController,
    initialStatusType: StatusType,
    initialLargeCategory: LargeCategoryEnum,
    paymentMethodId: String?,
    viewModel: PaymentMethodExpensesViewModel = koinViewModel {
        parametersOf(initialStatusType, initialLargeCategory, paymentMethodId)
    }
) {
    BaseScreen(
        viewModel = viewModel,
        onBack = { navController.popBackStack() }
    ) { uiState ->
        PaymentMethodExpensesContent(
            uiState = uiState,
            onBackClick = { navController.popBackStack() },
            onUpdateStatusType = viewModel::updateStatusType,
            onLoadNextPage = viewModel::loadNextPage,
            onClickHistory = { historyId ->
                navController.navigate("historyDetail/${uiState.largeCategory.name}/$historyId")
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaymentMethodExpensesContent(
    uiState: PaymentMethodExpensesUiState,
    onBackClick: () -> Unit,
    onUpdateStatusType: (StatusType) -> Unit,
    onLoadNextPage: () -> Unit,
    onClickHistory: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowStatusTypeModal by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // 페이징 트리거: 리스트 끝 부분 도달 시 다음 페이지 로드
    val shouldLoadNextPage by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadNextPage) {
        if (shouldLoadNextPage) {
            onLoadNextPage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(ColorGray.White)
    ) {
        WMTopBar(
            title = TopBarItem.Title("${uiState.largeCategory.label} 상세"),
            readingItem = TopBarItem.ReadingItem(
                action = onBackClick
            ),
            trailingCustomItem = TopBarItem.TrailingCustomItem {
                PreparednessStatusFilter(
                    statusTypeLabel = uiState.statusType.label,
                    largeCategoryLabel = null,
                    onStatusTypeClick = { isShowStatusTypeModal = true },
                    onLargeCategoryClick = null
                )
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            item {
                PaymentMethodExpensesHeader(
                    statusType = uiState.statusType,
                    paymentMethod = uiState.paymentMethod,
                    totalAmount = uiState.totalAmount,
                    diffAmount = uiState.diffAmount,
                )
            }

            if (uiState.histories.isEmpty() && !uiState.isPagingLoading) {
                item {
                    EmptyListView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentText = "내역이 없습니다."
                    )
                }
            } else {
                uiState.groupedHistories.forEach { (date, histories) ->
                    stickyHeader(key = "header_$date") {
                        DateHeader(
                            date = date,
                            format = formatDateKorMDE
                        )
                    }

                    items(histories, key = { it.history.id }) { history ->
                        CategoryExpensesItem(
                            history = history,
                            onClickHistory = onClickHistory
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (uiState.isPagingLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        if (isShowStatusTypeModal) {
            WMListSelectionModalBottomSheet(
                title = "기간 선택",
                items = StatusType.entries,
                selectedItem = uiState.statusType,
                itemLabel = { it.label },
                onItemSelected = {
                    onUpdateStatusType(it)
                    isShowStatusTypeModal = false
                },
                onDismissRequest = { isShowStatusTypeModal = false }
            )
        }
    }
}

@Preview
@Composable
private fun PaymentMethodExpensesContentPreview() {
    WMTheme {
        PaymentMethodExpensesContent(
            uiState = PaymentMethodExpensesUiState(
                statusType = StatusType.MONTH,
                largeCategory = LargeCategoryEnum.EXPENSES,
                paymentMethod = PaymentMethodVo(
                    id = "1",
                    label = "현대카드",
                    groupId = null,
                    groupLabel = null,
                    sort = 0L
                ),
                totalAmount = 1250000,
                lastTotalAmount = 1100000,
                histories = listOf(
                    HistoryWithDetails(
                        history = HistoryEntity(
                            id = "1",
                            largeCategory = LargeCategoryEnum.EXPENSES.name,
                            date = today.toEpochMilliseconds(),
                            amount = 55000,
                            content = "백화점 쇼핑"
                        ),
                        category = null,
                        paymentMethod = null,
                        repeatCycle = null,
                        installment = null
                    ),
                    HistoryWithDetails(
                        history = HistoryEntity(
                            id = "2",
                            largeCategory = LargeCategoryEnum.EXPENSES.name,
                            date = today.toEpochMilliseconds(),
                            amount = 12000,
                            content = "택시비"
                        ),
                        category = null,
                        paymentMethod = null,
                        repeatCycle = null,
                        installment = null
                    )
                )
            ),
            onBackClick = {},
            onUpdateStatusType = {},
            onLoadNextPage = {},
            onClickHistory = {}
        )
    }
}
