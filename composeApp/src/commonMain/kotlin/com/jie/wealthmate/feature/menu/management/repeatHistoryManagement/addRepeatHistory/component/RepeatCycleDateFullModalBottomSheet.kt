package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.utils.default
import kotlin.math.max

@Composable
fun RepeatCycleDateFullModalBottomSheet(
    repeatCycleDateMonth: Int?,
    repeatCycleDateDay: Int?,
    onConfirmClick: (Int, Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val listStateMonth = rememberLazyListState()
    val listStateDay = rememberLazyListState()
    var tempSelectedRepeatCycleMonth by remember { mutableStateOf(repeatCycleDateMonth ?: 1) }
    var tempSelectedRepeatCycleDay by remember { mutableStateOf(repeatCycleDateDay ?: 1) }

    val repeatCycleDateMonthItems = remember {
        (1..12).map { it to "${it}월" }
    }

    // 선택된 월에 따라 일수 계산 (윤년 고려하여 2월은 29일까지 허용)
    val repeatCycleDateDayItems = remember(tempSelectedRepeatCycleMonth) {
        val days = when (tempSelectedRepeatCycleMonth) {
            2 -> 29
            4, 6, 9, 11 -> 30
            else -> 31
        }
        (1..days).map { it to "${it}일" }
    }

    // 월 변경 시 선택된 일자가 해당 월의 최대 일수를 넘어가면 마지막 일로 조정
    LaunchedEffect(repeatCycleDateDayItems) {
        if (repeatCycleDateDayItems.isEmpty()) return@LaunchedEffect
        val maxDay = repeatCycleDateDayItems.last().first
        if (tempSelectedRepeatCycleDay > maxDay) {
            tempSelectedRepeatCycleDay = maxDay
        }
    }

    LaunchedEffect(Unit) {
        val indexMonth =
            repeatCycleDateMonthItems.indexOfFirst { it.first == tempSelectedRepeatCycleMonth }
        if (indexMonth >= 0) {
            listStateMonth.scrollToItem(max(0, indexMonth - 1))
        }

        val indexDay =
            repeatCycleDateDayItems.indexOfFirst { it.first == tempSelectedRepeatCycleDay }
        if (indexDay >= 0) {
            listStateDay.scrollToItem(max(0, indexDay - 1))
        }
    }

    WMModalBottomSheet(
        title = "반복 날짜",
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Padding.BackgroundHorizontal)
                .height(300.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RepeatCycleDateFullList(
                modifier = Modifier.weight(1f),
                listState = listStateMonth,
                repeatCycleDateItems = repeatCycleDateMonthItems,
                tempSelectedRepeatCycleDate = tempSelectedRepeatCycleMonth,
                onRepeatCycleClick = { tempSelectedRepeatCycleMonth = it }
            )

            VerticalDivider(
                modifier = Modifier
                    .padding(vertical = Padding.SpacerM)
                    .height(240.dp),
                color = ColorGray.Gray_200
            )

            RepeatCycleDateFullList(
                modifier = Modifier.weight(1f),
                listState = listStateDay,
                repeatCycleDateItems = repeatCycleDateDayItems,
                tempSelectedRepeatCycleDate = tempSelectedRepeatCycleDay,
                onRepeatCycleClick = { tempSelectedRepeatCycleDay = it }
            )
        }

        WMButton(
            text = "확인",
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.BackgroundHorizontal),
            buttonSize = ButtonSize.LARGE,
            onClick = {
                onConfirmClick(tempSelectedRepeatCycleMonth, tempSelectedRepeatCycleDay)
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun RowScope.RepeatCycleDateFullList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    repeatCycleDateItems: List<Pair<Int, String>>,
    tempSelectedRepeatCycleDate: Int? = null,
    onRepeatCycleClick: (Int) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(
            count = repeatCycleDateItems.size,
            key = { index -> repeatCycleDateItems[index].first },
        ) { index ->
            val repeatCycleDate = repeatCycleDateItems[index]

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
            .height(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
                fontSize = if (isSelected) 18.sp else 16.sp
            ),
        )
    }
}
