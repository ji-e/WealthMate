package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Typography
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
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary

@Composable
fun RepeatCycleDateModalBottomSheet(
    repeatCycleDate: Long?,
    repeatCycleDateItems: List<Pair<Long, String>>,
    onConfirmClick: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val listState = rememberLazyListState()
    var tempSelectedRepeatCycleDate by remember { mutableStateOf(repeatCycleDate) }

    LaunchedEffect(Unit) {
        val index = repeatCycleDateItems.map { it.first }.indexOf(tempSelectedRepeatCycleDate)
        val movePosition = if (index <= 0) 0 else index - 1

        listState.scrollToItem(movePosition)
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
            RepeatCycleDateList(
                listState = listState,
                repeatCycleDateItems = repeatCycleDateItems,
                tempSelectedRepeatCycleDate = tempSelectedRepeatCycleDate,
                onRepeatCycleClick = {
                    tempSelectedRepeatCycleDate = it
                    onDismissRequest()
                    onConfirmClick(tempSelectedRepeatCycleDate)
                }
            )
        }
    }
}

@Composable
private fun RepeatCycleDateList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    repeatCycleDateItems: List<Pair<Long, String>>,
    tempSelectedRepeatCycleDate: Long? = null,
    onRepeatCycleClick: (Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
    ) {
        items(
            count = repeatCycleDateItems.size,
            key = { index -> repeatCycleDateItems[index] },
        ) {
            val repeatCycleDate = repeatCycleDateItems[it]

            RepeatCycleDateItem(
                label = repeatCycleDate.second,
                isSelected = repeatCycleDate.first == tempSelectedRepeatCycleDate,
                onClick = { onRepeatCycleClick(repeatCycleDate.first) }
            )
        }
    }
}

@Composable
private fun RepeatCycleDateItem(
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