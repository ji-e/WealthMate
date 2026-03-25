package com.jie.wealthmate.feature.home.categoryExpenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.database.eneity.HistoryEntity
import com.jie.wealthmate.database.eneity.HistoryWithDetails
import com.jie.wealthmate.feature.calendar.START_DATE
import com.jie.wealthmate.feature.calendar.component.SelectedCalendarModalBottomSheet
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.categoryExpenses.component.CategoryExpensesHeader
import com.jie.wealthmate.feature.home.categoryExpenses.component.CategoryExpensesItem
import com.jie.wealthmate.feature.home.preparednessStatus.component.PreparednessStatusFilter
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.search.component.DateHeader
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateKorMDE
import com.jie.wealthmate.utils.formatDateKorYM
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down

@Composable
fun CategoryExpensesScreen(
    navController: NavController,
    selectedDate: String? = null,
    initialStatusType: StatusType,
    initialLargeCategory: LargeCategoryEnum,
    categoryId: String?,
    viewModel: CategoryExpensesViewModel = koinViewModel {
        parametersOf(selectedDate, initialStatusType, initialLargeCategory, categoryId)
    },
) {
    BaseScreen(
        viewModel = viewModel,
        onBack = { navController.popBackStack() }
    ) { uiState ->
        CategoryExpensesContent(
            uiState = uiState,
            onBackClick = { navController.popBackStack() },
            onUpdateStatusType = viewModel::updateStatusType,
            onUpdateSelectedMonth = viewModel::updateSelectedMonth,
            onLoadNextPage = viewModel::loadNextPage,
            onClickHistory = { historyId ->
                navController.navigate("historyDetail/${uiState.largeCategory.name}/$historyId")
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryExpensesContent(
    uiState: CategoryExpensesUiState,
    onBackClick: () -> Unit,
    onUpdateStatusType: (StatusType) -> Unit,
    onUpdateSelectedMonth: (LocalDate) -> Unit,
    onLoadNextPage: () -> Unit,
    onClickHistory: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowStatusTypeModal by remember { mutableStateOf(false) }
    var isShowSelectedCalendarModalBottomSheet by remember { mutableStateOf(false) }
    val monthItems = remember(START_DATE) {
        val totalMonths = (today.year - START_DATE.year) * 12 + 12
        List(totalMonths) { START_DATE.plus(it, DateTimeUnit.MONTH) }
    }
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
                if (uiState.isHome) {
                    // 홈에서 왔을 때
                    PreparednessStatusFilter(
                        statusTypeLabel = uiState.statusType.label,
                        largeCategoryLabel = null,
                        onStatusTypeClick = { isShowStatusTypeModal = true },
                        onLargeCategoryClick = null
                    )
                } else {
                    // 홈이 아닌 화면에서 왔을 때
                    Row(
                        modifier = Modifier.noRippleClickable {
                            isShowSelectedCalendarModalBottomSheet = true
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WMText(
                            text = uiState.selectedMonth.convertLocalDateToString(formatDateKorYM),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_drop_down),
                            contentDescription = "날짜 선택",
                            modifier = Modifier.size(24.dp),
                            tint = ColorGray.Gray_700
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            item {
                CategoryExpensesHeader(
                    statusType = uiState.statusType,
                    category = uiState.category,
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
                        WMSpacer(size = SpacerSize.X_SMALL)
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

        // 월 선택 바텀시트
        if (isShowSelectedCalendarModalBottomSheet && uiState.selectedMonth != null) {
            SelectedCalendarModalBottomSheet(
                monthItem = monthItems,
                selectedMonth = uiState.selectedMonth,
                onMonthChange = { month ->
                    onUpdateSelectedMonth(month)
                    isShowSelectedCalendarModalBottomSheet = false
                },
                onDismissRequest = { isShowSelectedCalendarModalBottomSheet = false }
            )
        }
    }
}

@Preview
@Composable
private fun CategoryExpensesContentPreview() {
    WMTheme {
        CategoryExpensesContent(
            uiState = CategoryExpensesUiState(
                statusType = StatusType.MONTH,
                largeCategory = LargeCategoryEnum.EXPENSES,
                category = CategoryVo(
                    id = "1",
                    middleLabel = "식비",
                    icon = "🍔",
                    largeCategory = LargeCategoryEnum.EXPENSES,
                    sort = 0L,
                    isFixed = false,
                    tags = persistentListOf()
                ),
                totalAmount = 450000,
                lastTotalAmount = 400000,
                histories = listOf(
                    HistoryWithDetails(
                        history = HistoryEntity(
                            id = "1",
                            largeCategory = LargeCategoryEnum.EXPENSES.name,
                            date = today.toEpochMilliseconds(),
                            amount = 12000,
                            content = "점심 식사"
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
            onUpdateSelectedMonth = {},
            onLoadNextPage = {},
            onClickHistory = {}
        )
    }
}
