package com.jie.wealthmate.feature.calendar.listCalendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListCalendar(
    selectedDate: LocalDate,
    historyItems: List<HistoryVo>,
    onDateSelected: (LocalDate) -> Unit,
    onHistoryClick: (HistoryVo) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    // 1. 데이터 가공 최적화: 전체 그룹화와 선택 날짜 병합 분리
    val historyGroups = remember(historyItems) {
        historyItems.groupBy { it.date }
    }

    val groupedItems = remember(historyGroups, selectedDate) {
        val groups = historyGroups.toMutableMap()
        // 데이터가 없더라도 오늘과 선택된 날짜는 섹션을 생성함
        if (!groups.containsKey(today)) groups[today] = emptyList()
        if (!groups.containsKey(selectedDate)) groups[selectedDate] = emptyList()

        groups.toList().sortedByDescending { it.first }
    }

    // 각 섹션의 시작 인덱스 계산 (스크롤 동기화용)
    val groupStartIndices = remember(groupedItems) {
        var cumulativeIndex = 0
        groupedItems.map { (date, items) ->
            val startIndex = cumulativeIndex
            val rowCount = if (items.isEmpty()) 1 else items.size
            cumulativeIndex += rowCount + 2 // header(1) + items/empty(rowCount) + spacer(1)
            startIndex to date
        }
    }

    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var isInitialScroll by remember { mutableStateOf(true) }

    // 최신 상태를 참조하기 위해 rememberUpdatedState 사용
    val currentSelectedDate by rememberUpdatedState(selectedDate)
    val currentOnDateSelected by rememberUpdatedState(onDateSelected)

    // 2. 외부(상단 캘린더 등)에서 날짜 변경 시 리스트 스크롤 동기화
    LaunchedEffect(selectedDate, groupStartIndices) {
        if (groupStartIndices.isEmpty()) return@LaunchedEffect

        val firstVisibleIndex = listState.firstVisibleItemIndex
        val currentVisibleDate =
            groupStartIndices.lastOrNull { it.first <= firstVisibleIndex }?.second

        // 이미 해당 날짜가 최상단이면 스크롤 불필요
        if (currentVisibleDate == selectedDate) {
            isInitialScroll = false
            return@LaunchedEffect
        }

        // 사용자가 스크롤 중이면 외부 강제 스크롤 차단 (초기 스크롤 제외)
        if (listState.isScrollInProgress && !isInitialScroll) return@LaunchedEffect

        val targetIndex =
            groupStartIndices.find { it.second == selectedDate }?.first ?: return@LaunchedEffect

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

    // 3. 리스트 스크롤 위치를 외부 상태(selectedDate)로 동기화
    LaunchedEffect(listState, groupStartIndices) {
        snapshotFlow {
            val firstIndex = listState.firstVisibleItemIndex
            val isScrollInProgress = listState.isScrollInProgress

            // 리스트 하단 끝에 도달했는지 확인
            val isAtBottom = !listState.canScrollForward && listState.canScrollBackward

            val dateAtTop = if (isAtBottom) {
                groupStartIndices.lastOrNull()?.second
            } else {
                groupStartIndices.lastOrNull { it.first <= firstIndex }?.second
            }

            dateAtTop to (isScrollInProgress && !isProgrammaticScroll)
        }
            .filter { it.second } // 사용자가 직접 스크롤 중일 때만
            .map { it.first }
            .distinctUntilChanged()
            .collect { date ->
                if (date != null && date != currentSelectedDate) {
                    currentOnDateSelected(date)
                }
            }
    }

    if (groupedItems.isEmpty()) {
        EmptyListView(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            contentText = "내역이 없습니다.",
        )
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            groupedItems.forEach { (date, items) ->
                stickyHeader(key = "header_$date") {
                    DateHeader(date = date)
                }

                if (items.isEmpty()) {
                    item(key = "empty_$date") {
                        EmptyListView(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentText = "내역이 없습니다.",
                        )
                    }
                } else {
                    items(
                        items = items,
                        key = { it.id }
                    ) { history ->
                        HistoryItem(
                            history = history,
                            onItemClick = { onHistoryClick(history) }
                        )
                    }
                }

                item(key = "spacer_$date") {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListCalendarPreview() {
    WMTheme {
        ListCalendar(
            selectedDate = today,
            historyItems = emptyList(),
            onDateSelected = {},
            onHistoryClick = {}
        )
    }
}
