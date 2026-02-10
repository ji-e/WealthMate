package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Typography
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.utils.default

@Composable
fun RepeatCycleDateFullModalBottomSheet(
    repeatCycleDateMonth: Long?,
    repeatCycleDateDay: Long?,
    onConfirmClick: (Long, Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val listStateMonth = rememberLazyListState()
    val listStateDay = rememberLazyListState()
    var tempSelectedRepeatCycleMonth by remember { mutableStateOf(repeatCycleDateMonth) }
    var tempSelectedRepeatCycleDay by remember { mutableStateOf(repeatCycleDateDay) }

    val repeatCycleDateMonthItems = listOf(
        1L to "1월",
        2L to "2월",
        3L to "3월",
        4L to "4월",
        5L to "5월",
        6L to "6월",
        7L to "7월",
        8L to "8월",
        9L to "9월",
        10L to "10월",
        11L to "11월",
        12L to "12월",
    )


    // 선택된 월에 따라 일수 계산 (윤년 고려하여 2월은 29일까지 허용)
    val repeatCycleDateDayItems = remember(tempSelectedRepeatCycleMonth) {
        val month = tempSelectedRepeatCycleMonth ?: return@remember emptyList()
        val days = when (month) {
            2L -> 29
            4L, 6L, 9L, 11L -> 30
            else -> 31
        }
        (1..days).map { it.toLong() to "${it}일" }
    }

    // 월 변경 시 선택된 일자가 해당 월의 최대 일수를 넘어가면 마지막 일로 조정
    LaunchedEffect(repeatCycleDateDayItems) {
        if (repeatCycleDateDayItems.isEmpty()) return@LaunchedEffect
        val maxDay = repeatCycleDateDayItems.last().first
        if (tempSelectedRepeatCycleDay.default() > maxDay) {
            tempSelectedRepeatCycleDay = maxDay
        }
    }

    LaunchedEffect(Unit) {
        val indexMonth =
            repeatCycleDateMonthItems.map { it.first }.indexOf(tempSelectedRepeatCycleMonth)
        val movePositionMonth = if (indexMonth <= 0) 0 else indexMonth - 1

        listStateMonth.scrollToItem(movePositionMonth)

        val indexDay = repeatCycleDateDayItems.map { it.first }.indexOf(tempSelectedRepeatCycleDay)
        val movePositionDay = if (indexDay <= 0) 0 else indexDay - 1

        listStateDay.scrollToItem(movePositionDay)
    }

    WMModalBottomSheet(
        title = "반복 날짜",
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .height(300.dp)
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                RepeatCycleDateFullList(
                    listState = listStateMonth,
                    repeatCycleDateItems = repeatCycleDateMonthItems,
                    tempSelectedRepeatCycleDate = tempSelectedRepeatCycleMonth,
                    onRepeatCycleClick = {
                        tempSelectedRepeatCycleMonth = it
                    }
                )

                VerticalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = ColorGray.Gray_50
                )

                RepeatCycleDateFullList(
                    listState = listStateDay,
                    repeatCycleDateItems = repeatCycleDateDayItems,
                    tempSelectedRepeatCycleDate = tempSelectedRepeatCycleDay,
                    onRepeatCycleClick = {
                        tempSelectedRepeatCycleDay = it
                    }
                )
            }
            WMButton(
                text = "수정",
                buttonStyle = ButtonStyle.FILLED,
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                enabled = tempSelectedRepeatCycleMonth != null && tempSelectedRepeatCycleDay != null,
                onClick = {
                    val month = tempSelectedRepeatCycleMonth ?: return@WMButton
                    val day = tempSelectedRepeatCycleDay ?: return@WMButton
                    onConfirmClick(month, day)
                    onDismissRequest()
                }
            )
        }
    }
}

@Composable
private fun RowScope.RepeatCycleDateFullList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    repeatCycleDateItems: List<Pair<Long, String>>,
    tempSelectedRepeatCycleDate: Long? = null,
    onRepeatCycleClick: (Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.weight(1f),
        state = listState,
    ) {
        items(
            count = repeatCycleDateItems.size,
            key = { index -> repeatCycleDateItems[index].first },
        ) {
            val repeatCycleDate = repeatCycleDateItems[it]

            RepeatCycleDateFullItem(
                label = repeatCycleDate.second,
                isSelected = repeatCycleDate.first == tempSelectedRepeatCycleDate,
                onClick = { onRepeatCycleClick(repeatCycleDate.first) }
            )
        }
    }
}

@Composable
private fun RepeatCycleDateFullItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(CircleShape)
            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = label,
            style = Typography().bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
                fontSize = if (isSelected) 18.sp else 16.sp
            ),
        )
    }
}
