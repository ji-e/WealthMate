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
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListCalendar(
    selectedDate: LocalDate,
    historyItems: List<HistoryVo>,
    onDateSelected: (LocalDate) -> Unit, // 날짜가 변경되었을 때 호출될 콜백
    onHistoryClick: (HistoryVo) -> Unit,
) {
    // 1. 데이터를 날짜별 내림차순으로 정렬 및 그룹화
    val groupedItems = remember(historyItems) {
        historyItems
            .groupBy { it.date }
            .toList()
            .sortedByDescending { (date, _) -> date }
    }

    // 2. 각 날짜 그룹의 시작 인덱스를 미리 계산
    val groupStartIndices = remember(groupedItems) {
        var cumulativeIndex = 0
        groupedItems.map { (date, items) ->
            val startIndex = cumulativeIndex
            cumulativeIndex += items.size + 1 // 헤더(1) + 아이템 개수
            startIndex to date
        }
    }

    val listState = rememberLazyListState()
    var isProgrammaticScroll by remember { mutableStateOf(false) }
    var isInitialScroll by remember { mutableStateOf(true) }


    // 4. 외부(월 캘린더 등)에서 날짜 변경 시 스크롤 이동
    LaunchedEffect(
        key1 = selectedDate,
        key2 = groupStartIndices
    ) {
        val targetIndex = groupStartIndices.find { it.second == selectedDate }?.first
        if (targetIndex != null) {
            // 현재 리스트의 첫 번째 아이템이 이미 목표 인덱스라면 스크롤 건너뜀 (루프 방지)
            if (listState.firstVisibleItemIndex == targetIndex) return@LaunchedEffect

            try {
                isProgrammaticScroll = true
                if (isInitialScroll) {
                    listState.scrollToItem(index = targetIndex)
                    isInitialScroll = false
                } else {
                    listState.animateScrollToItem(index = targetIndex)
                }
            } finally {
                // 애니메이션이 확실히 끝날 때까지 대기하거나
                // 스크롤이 완전히 멈춘 것을 확인하기 위해 약간의 지연을 줄 수 있습니다.
                isProgrammaticScroll = false
            }
        }
    }


    // 5. [수정] 사용자가 리스트를 직접 스크롤할 때만 상단 날짜 반영
    LaunchedEffect(
        key1 = listState,
        key2 = groupStartIndices
    ) {
        snapshotFlow {
            // 스크롤 중인지 여부와 현재 인덱스를 함께 관찰
            Triple(
                listState.firstVisibleItemIndex,
                listState.isScrollInProgress,
                listState.canScrollForward
            )
        }
            .collect { (firstIndex, isScrolling, canScrollForward) ->
                // 프로그램에 의한 스크롤이 아니고, '실제로 사용자가 스크롤 중'일 때만 업데이트
                if (isProgrammaticScroll.not() && isScrolling) {
                    val dateAtTop = if (canScrollForward.not() && listState.canScrollBackward) {
                        groupStartIndices.lastOrNull()?.second
                    } else {
                        groupStartIndices.lastOrNull { it.first <= firstIndex }?.second
                    }

                    if (dateAtTop != null) {
                        onDateSelected(dateAtTop)
                    }
                }
            }
    }

    if (groupedItems.isEmpty()) {
        EmptyListView(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 28.dp),
            contentText = "내역이 없습니다.",
        )
        return
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {


        groupedItems.forEach { (date, items) ->
            stickyHeader(
                key = "header_$date"
            ) {
                DateHeader(date = date)
            }

            items(
                items = items,
                key = { it.id }
            ) { item ->
                HistoryItem(
                    history = item,
                    onItemClick = {
                        onHistoryClick(item)
                    }
                )
            }
        }
    }
}