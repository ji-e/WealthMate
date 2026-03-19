package com.jie.wealthmate.component


import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme


/**
 * 다중 항목 선택을 위한 바텀 시트
 */
@Composable
fun <T> WMMultiListSelectionModalBottomSheet(
    title: String,
    items: List<T>,
    selectedItems: List<T>,
    itemLabel: (T) -> String,
    onItemsSelected: (List<T>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var tempSelectedItems by remember { mutableStateOf(selectedItems.toSet()) }
    val isAllSelected = items.isNotEmpty() && items.all { it in tempSelectedItems }

    WMModalBottomSheet(
        title = title,
        onDismissRequest = onDismissRequest,
    ) {
        BoxWithConstraints {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight * 0.6f)
            ) {
                if (items.isNotEmpty()) {
                    item {
                        WMCheckBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = Padding.BackgroundHorizontal),
                            iconModifier = Modifier.size(24.dp),
                            label = "모두 선택",
                            labelStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            checked = isAllSelected,
                            onCheckedChange = { checked ->
                                tempSelectedItems = if (checked) items.toSet() else emptySet()
                            }
                        )
                        WMHorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = Padding.BackgroundHorizontal,
                                vertical = 4.dp
                            )
                        )
                    }
                }

                items(items) { item ->
                    val isSelected = tempSelectedItems.contains(item)

                    WMCheckBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(horizontal = Padding.BackgroundHorizontal),
                        iconModifier = Modifier.size(24.dp),
                        label = itemLabel(item),
                        labelStyle = MaterialTheme.typography.bodyLarge,
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            tempSelectedItems =
                                if (checked) tempSelectedItems + item
                                else tempSelectedItems - item

                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        WMButton(
            text = "확인",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = Padding.BackgroundHorizontal)
                .padding(bottom = Padding.BackgroundBottom)
                .fillMaxWidth(),
            onClick = {
                onItemsSelected(tempSelectedItems.toList())
                onDismissRequest()
            }
        )
    }
}

@Preview
@Composable
private fun WMMultiListSelectionModalBottomSheetPreview() {
    val items = listOf("사과", "바나나", "포도", "오렌지", "수박")
    WMTheme {
        Column {
            WMMultiListSelectionModalBottomSheet(
                title = "다중 선택 테스트",
                items = items,
                selectedItems = listOf(items[0], items[2]),
                itemLabel = { it },
                onItemsSelected = {},
                onDismissRequest = {}
            )
        }
    }
}

