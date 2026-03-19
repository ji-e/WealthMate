package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme

/**
 * 단일 항목 선택을 위한 바텀 시트
 */
@Composable
fun <T> WMListSelectionModalBottomSheet(
    title: String,
    items: List<T>,
    selectedItem: T?,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var tempSelectedItem by remember { mutableStateOf(selectedItem) }

    WMModalBottomSheet(
        title = title,
        onDismissRequest = onDismissRequest,
    ) {
        BoxWithConstraints {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight * 0.2f, max = maxHeight * 0.6f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(items) { item ->
                    val isSelected = item == tempSelectedItem

                    Box(
                        modifier = Modifier
                            .padding(horizontal = Padding.BackgroundHorizontal)
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
                            .clickable {
                                tempSelectedItem = item
                                onItemSelected(item)
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = itemLabel(item),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) ColorSetting.Primary else ColorSetting.Default,
                            fontSize = if (isSelected) 18.sp else 16.sp
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun WMListSelectionModalBottomSheetPreview() {
    val items = listOf("항목 1", "항목 2", "항목 3", "항목 4")
    WMTheme {
        Column {
            WMListSelectionModalBottomSheet(
                title = "단일 선택 테스트",
                items = items,
                selectedItem = items[1],
                itemLabel = { it },
                onItemSelected = {},
                onDismissRequest = {}
            )
        }
    }
}

