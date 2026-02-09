package com.jie.wealthmate.feature.calendar.component.listCalendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListCalendar(
    selectedDate: LocalDate,
    historyItems: List<HistoryVo>,
    onDateSelected: (LocalDate) -> Unit,
    onHistoryClick: (HistoryVo) -> Unit,
) {
    // 1. 데이터 가공 결과 캐싱
    val groupedItems = remember(historyItems) {
        historyItems
            .groupBy { it.date }
            .toList()
            .sortedByDescending { (date, _) -> date }
    }

    val groupStartIndices = remember(groupedItems) {
        var cumulativeIndex = 0
        groupedItems.map { (date, items) ->
            val startIndex = cumulativeIndex
            cumulativeIndex += items.size + 1
            startIndex to date
        }
    }

    val listState = rememberLazyListState()
    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var isInitialScroll by remember { mutableStateOf(true) }

    // 2. 외부 날짜 변경 시 동기화 스크롤
    LaunchedEffect(selectedDate, groupStartIndices) {
        val targetIndex = groupStartIndices.find { it.second == selectedDate }?.first ?: return@LaunchedEffect
        
        // 이미 해당 위치 근처라면 무시
        if (listState.firstVisibleItemIndex == targetIndex) return@LaunchedEffect
        
        val currentVisibleDate = groupStartIndices.lastOrNull { it.first <= listState.firstVisibleItemIndex }?.second
        if (listState.isScrollInProgress && currentVisibleDate == selectedDate) return@LaunchedEffect

        try {
            isProgrammaticScroll = true
            if (isInitialScroll) {
                listState.scrollToItem(targetIndex)
                isInitialScroll = false
            } else {
                listState.animateScrollToItem(targetIndex)
            }
        } finally {
            isProgrammaticScroll = false
        }
    }

    // 3. 스크롤 위치를 외부 날짜 상태로 동기화 (최적화)
    LaunchedEffect(listState, groupStartIndices) {
        snapshotFlow {
            val firstIndex = listState.firstVisibleItemIndex
            val canScrollForward = listState.canScrollForward
            if (!canScrollForward && listState.canScrollBackward) {
                groupStartIndices.lastOrNull()?.second
            } else {
                groupStartIndices.lastOrNull { it.first <= firstIndex }?.second
            }
        }
        .filter { isProgrammaticScroll.not() && listState.isScrollInProgress }
        .distinctUntilChanged() // 날짜가 실제로 바뀔 때만 콜백 호출
        .collect { date ->
            if (date != null) onDateSelected(date)
        }
    }

    if (groupedItems.isEmpty()) {
        EmptyListView(
            modifier = Modifier.fillMaxSize().padding(vertical = 20.dp, horizontal = 28.dp),
            contentText = "내역이 없습니다.",
        )
        return
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        groupedItems.forEach { (date, items) ->
            stickyHeader(key = "header_$date") {
                DateHeader(date = date)
            }

            items(
                items = items,
                key = { it.id } // Stable ID 사용
            ) { item ->
                HistoryItem(
                    history = item,
                    onItemClick = { onHistoryClick(item) }
                )
            }
        }
    }
}
