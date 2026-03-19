package com.jie.wealthmate.feature.calendar.component.listCalendar

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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmptyListView
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
    // 1. 데이터 가공: selectedDate를 포함하여 내역이 없어도 해당 날짜 섹션이 생성되도록 함
    val groupedItems = remember(historyItems, selectedDate) {
        val groups = historyItems.groupBy { it.date }.toMutableMap()
        
        // 오늘 날짜와 현재 선택된 날짜는 데이터가 없더라도 빈 그룹으로 추가하여 헤더가 나오게 함
        if (!groups.containsKey(today)) groups[today] = emptyList()
        if (!groups.containsKey(selectedDate)) groups[selectedDate] = emptyList()
        
        groups.toList()
            .sortedByDescending { (date, _) -> date }
    }

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

    // 2. 외부(캘린더 등)에서 날짜 변경 시 리스트 스크롤
    LaunchedEffect(selectedDate, groupStartIndices) {
        if (groupStartIndices.isEmpty()) return@LaunchedEffect

        // 현재 리스트의 최상단에 보이는 아이템의 날짜를 확인
        val currentVisibleDate = groupStartIndices.lastOrNull { it.first <= listState.firstVisibleItemIndex }?.second

        // 만약 이미 선택된 날짜가 화면에 보이고 있다면 (다른 화면에서 돌아온 경우 포함), 
        // 강제로 스크롤 위치를 조정하지 않고 그대로 둡니다.
        if (currentVisibleDate == selectedDate) {
            isInitialScroll = false
            return@LaunchedEffect
        }

        // 사용자가 직접 스크롤 중인 경우, 외부 신호에 의한 강제 스크롤을 차단합니다.
        if (listState.isScrollInProgress && !isInitialScroll) return@LaunchedEffect

        val targetEntry = groupStartIndices.find { it.second == selectedDate } ?: return@LaunchedEffect
        val targetIndex = targetEntry.first

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

    // 3. 리스트 스크롤 위치를 캘린더 상태로 동기화
    LaunchedEffect(listState, groupStartIndices, selectedDate) {
        snapshotFlow {
            val firstIndex = listState.firstVisibleItemIndex
            val canScrollForward = listState.canScrollForward
            val canScrollBackward = listState.canScrollBackward
            val isScrollInProgress = listState.isScrollInProgress

            val date = if (!canScrollForward && canScrollBackward) {
                groupStartIndices.lastOrNull()?.second
            } else {
                groupStartIndices.lastOrNull { it.first <= firstIndex }?.second
            }
            Triple(date, isScrollInProgress, isProgrammaticScroll)
        }
        .filter { (_, isScrolling, isProgrammatic) -> isScrolling && !isProgrammatic }
        .map { it.first }
        .distinctUntilChanged()
        .collect { date ->
            if (date != null && date != selectedDate) {
                onDateSelected(date)
            }
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
                ) { item ->
                    HistoryItem(
                        history = item,
                        onItemClick = { onHistoryClick(item) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
