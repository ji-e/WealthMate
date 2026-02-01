package com.jie.wealthmate.feature.calendar.component.listCalendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListCalendar(
    selectedDate: LocalDate,
    historyItems: List<HistoryVo>,
    onDateSelected: (LocalDate) -> Unit, // 날짜가 변경되었을 때 호출될 콜백 추가
    onHistoryClick: (HistoryVo) -> Unit,
) {
    // 1. 데이터를 날짜별 내림차순으로 정렬 및 그룹화 (KMP 호환 방식)
    val groupedItems = remember(historyItems) {
        historyItems
            .groupBy { it.date }
            .toList()
            .sortedByDescending { (date, _) -> date }
    }

    // 2. 각 날짜 그룹의 시작 인덱스를 미리 계산 (추측: 스크롤 위치와 날짜를 매핑하기 위함)
    // 예: 10월 26일(인덱스 0), 10월 25일(인덱스 3) 등
    val groupStartIndices = remember(groupedItems) {
        var cumulativeIndex = 0
        groupedItems.map { (date, items) ->
            val startIndex = cumulativeIndex
            cumulativeIndex += items.size + 1 // 헤더(1) + 아이템 개수
            startIndex to date
        }
    }

    val listState = rememberLazyListState()

    // 3. 프로그램에 의한 스크롤인지 구분하기 위한 플래그 (추측: 외부 변경과 내부 스크롤 간의 무한 루프 방지)
    var isProgrammaticScroll by remember { mutableStateOf(false) }

    // 4. selectedDate가 외부(예: 캘린더)에서 변경될 때 해당 위치로 스크롤
    LaunchedEffect(
        key1 = selectedDate
    ) {
        // 외부에서 날짜가 바뀌었을 때애니메이션 실행

        val targetIndex = groupStartIndices.find { it.second == selectedDate }?.first
        if (targetIndex != null) {
            try {
                isProgrammaticScroll = true
                listState.animateScrollToItem(index = targetIndex)
            } finally {
                // 애니메이션이 완료되거나 중단되면 플래그 해제
                isProgrammaticScroll = false
            }
        }
    }

    // 5. 리스트 스크롤 시 스티키 헤더의 날짜로 selectedDate 업데이트
    LaunchedEffect(
        key1 = listState,
        key2 = groupStartIndices
    ) {
        snapshotFlow {
            // firstVisibleItemIndex와 하단 도달 여부를 함께 관찰
            listState.firstVisibleItemIndex to listState.canScrollForward
        }
            .collect { (firstIndex, canScrollForward) ->
                // 프로그램에 의한 스크롤 중이 아닐 때만(즉, 사용자 스크롤 시에만) 업데이트 호출
                if (isProgrammaticScroll.not()) {


                    // 리스트 최하단에 도달했고(canScrollForward가 false),
                    // 위로 스크롤한 적이 있다면(canScrollBackward가 true) 마지막 날짜를 선택. (추측: 마지막 헤더가 상단에 닿지 못할 경우 대비)
                    val dateAtTop =
                        if (canScrollForward.not() && listState.canScrollBackward) {
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

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        groupedItems.forEach { (date, items) ->
            // 일자별 헤더
            stickyHeader(
                key = "header_$date"
            ) {
                DateHeader(date = date)
            }

            // 해당 일자의 내역 바디
            items(
                items = items,
                key = { it.id }
            ) { item ->
                HistoryItemRow(
                    item = item,
                    onItemClick = {
                        onHistoryClick(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun DateHeader(
    date: LocalDate,
) {
    WMText(
        text = "${date.year}년 ${date.month.number}월 ${date.day}일",
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    )
}

@Composable
private fun HistoryItemRow(
    item: HistoryVo,
    onItemClick: () -> Unit,
) {
    WMText(
        text = "${item.content} ${formatWithCommas(item.amount.toString())}원",
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(all = 16.dp)
    )
}