package com.jie.wealthmate.feature.home.paymentMethodExpenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.calendar.historyDetail.HistoryDetailScreen
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.categoryExpenses.component.CategoryExpensesItem
import com.jie.wealthmate.feature.home.paymentMethodExpenses.component.PaymentMethodExpensesHeader
import com.jie.wealthmate.feature.home.preparednessStatus.component.PreparednessStatusFilter
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.search.component.DateHeader
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.formatDateKorMDE
import com.jie.wealthmate.utils.toLocalDate
import org.koin.core.parameter.parametersOf

class PaymentMethodExpensesScreen(
    private val initialStatusType: StatusType,
    private val initialLargeCategory: LargeCategoryEnum,
    private val paymentMethodId: String?,
) : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: PaymentMethodExpensesScreenModel = koinScreenModel {
            parametersOf(initialStatusType, initialLargeCategory, paymentMethodId)
        }
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowStatusTypeModal by remember { mutableStateOf(false) }

        val groupedHistories = remember(uiState.histories) {
            uiState.histories
                .groupBy { it.history.date.toLocalDate() }
                .toList()
                .sortedByDescending { it.first }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColorGray.White)
        ) {
            WMTopBar(
                title = TopBarItem.Title("${uiState.largeCategory.label} 상세"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
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
            ) {
                item {
                    PaymentMethodExpensesHeader(
                        statusType = uiState.statusType,
                        paymentMethod = uiState.paymentMethod,
                        totalAmount = uiState.totalAmount,
                        diffAmount = uiState.diffAmount,
                    )
                }

                if (groupedHistories.isEmpty()) {
                    item {
                        EmptyListView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentText = "내역이 없습니다."
                        )
                    }
                } else {
                    groupedHistories.forEach { (date, histories) ->
                        stickyHeader(key = "header_$date") {
                            DateHeader(
                                date = date,
                                format = formatDateKorMDE
                            )
                        }

                        items(histories, key = { it.history.id }) { history ->
                            CategoryExpensesItem(
                                history = history,
                                onClickHistory = { historyId ->
                                    navigator.push(
                                        HistoryDetailScreen(
                                            largeCategory = uiState.largeCategory,
                                            historyId = historyId
                                        )
                                    )
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(4.dp))
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
                    onItemSelected = { screenModel.updateStatusType(it) },
                    onDismissRequest = { isShowStatusTypeModal = false }
                )
            }
        }
    }
}
