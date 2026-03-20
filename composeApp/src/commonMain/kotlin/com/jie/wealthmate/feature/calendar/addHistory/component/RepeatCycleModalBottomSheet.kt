package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
fun RepeatCycleModalBottomSheet(
    selectedRepeatCycle: RepeatCycleEnum?,
    onConfirmClick: (RepeatCycleEnum?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val listState = rememberLazyListState()
    val repeatCycleItems = RepeatCycleEnum.entries
    var tempSelectedRepeatCycle by remember { mutableStateOf(selectedRepeatCycle) }

    LaunchedEffect(Unit) {
        val index = repeatCycleItems.indexOf(tempSelectedRepeatCycle)
        val movePosition = if (index <= 0) 0 else index - 1

        listState.scrollToItem(movePosition)
    }

    WMModalBottomSheet(
        title = "반복 주기",
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
        ) {
            RepeatCycleList(
                listState = listState,
                repeatCycleItems = repeatCycleItems,
                tempSelectedRepeatCycle = tempSelectedRepeatCycle,
                onRepeatCycleClick = {
                    tempSelectedRepeatCycle = it
                    onDismissRequest()
                    onConfirmClick(tempSelectedRepeatCycle)
                }
            )
        }
    }
}

@Composable
fun RepeatCycleList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    repeatCycleItems: List<RepeatCycleEnum> = RepeatCycleEnum.entries,
    tempSelectedRepeatCycle: RepeatCycleEnum? = null,
    onRepeatCycleClick: (RepeatCycleEnum) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
    ) {
        items(
            count = repeatCycleItems.size,
            key = { index -> repeatCycleItems[index] },
        ) {
            val repeatCycle = repeatCycleItems[it]

            RepeatCycleItem(
                label = repeatCycle.label,
                isSelected = repeatCycle == tempSelectedRepeatCycle,
                onClick = { onRepeatCycleClick(repeatCycle) }
            )
        }
    }
}

@Composable
fun RepeatCycleItem(
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
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
            fontSize = if (isSelected) 18.sp else 16.sp,
        )
    }
}