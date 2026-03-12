package com.jie.wealthmate.feature.search.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.calendar.component.listCalendar.HistoryItem
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.search.SearchSortOrder
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.formatDateDotYYMDE
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResult(
    startDate: LocalDate?,
    endDate: LocalDate?,
    summary: ImmutableMap<LargeCategoryEnum, Long>,
    selectedLargeCategories: List<LargeCategoryEnum>,
    searchResults: List<HistoryVo>,
    sortOrder: SearchSortOrder,
    isLoading: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onHistoryClick: (HistoryVo) -> Unit,
) {
    if (searchResults.isEmpty() && !isLoading) {
        // 결과가 없을 때는 스크롤되지 않도록 Column 사용
        Column(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxSize()
        ) {
            SearchSummary(
                startDate = startDate,
                endDate = endDate,
                summary = summary,
                selectedLargeCategories = selectedLargeCategories,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            EmptyListView(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
                contentText = "검색 결과가 없습니다.",
            )
        }
    } else {
        val listState = rememberLazyListState()

        // 스크롤 끝에 도달했는지 확인
        val shouldLoadMore = remember {
            derivedStateOf {
                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                    ?: return@derivedStateOf false

                lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 5
            }
        }

        LaunchedEffect(shouldLoadMore.value) {
            if (shouldLoadMore.value && hasMore && !isLoading) {
                onLoadMore()
            }
        }

        val groupedItems = remember(searchResults, sortOrder) {
            if (sortOrder == SearchSortOrder.LATEST) {
                searchResults
                    .groupBy { it.date }
                    .toList()
                    .sortedByDescending { (date, _) -> date }
            } else {
                emptyList()
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxSize()
        ) {
            item {
                SearchSummary(
                    startDate = startDate,
                    endDate = endDate,
                    summary = summary,
                    selectedLargeCategories = selectedLargeCategories,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (sortOrder == SearchSortOrder.LATEST) {
                groupedItems.forEach { (date, items) ->
                    stickyHeader(key = "header_$date") {
                        DateHeader(date = date)
                    }

                    items(
                        items = items,
                        key = { it.id }
                    ) { item ->
                        HistoryItem(
                            history = item,
                            onItemClick = { onHistoryClick(item) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                items(
                    items = searchResults,
                    key = { it.id }
                ) { item ->
                    HistoryItem(
                        history = item,
                        onItemClick = { onHistoryClick(item) }
                    )
                }
            }

            if (isLoading) {
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
        }
    }
}

@Composable
fun DateHeader(
    date: LocalDate,
) {
    WMText(
        text = date.convertLocalDateToString(formatDateDotYYMDE),
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = ColorGray.Gray_500
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = ColorGray.White)
            .padding(horizontal = 28.dp)
            .padding(top = 12.dp, bottom = 4.dp)
    )
}
