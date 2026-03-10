package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
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
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary

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
                    .heightIn(max = maxHeight * 0.6f)
                    .padding(bottom = 8.dp)
            ) {
                items(items) { item ->
                    val isSelected = item == tempSelectedItem

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 28.dp)
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
                            .clickable {
                                tempSelectedItem = item
                                tempSelectedItem?.let { onItemSelected(it) }
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = itemLabel(item),
                            style = Typography().bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) ColorPrimary.Primary_700 else ColorGray.Gray_700,
                                fontSize = if (isSelected) 18.sp else 16.sp
                            ),
                        )
                    }
                }
            }
        }
    }
}

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
                                .height(44.dp)
                                .padding(horizontal = 28.dp),
                            iconModifier = Modifier.size(28.dp),
                            label = "모두 선택",
                            labelStyle = Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            checked = isAllSelected,
                            onCheckedChange = { checked ->
                                tempSelectedItems = if (checked) {
                                    tempSelectedItems + items
                                } else {
                                    tempSelectedItems - items.toSet()
                                }
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp),
                            color = ColorGray.Gray_200,
                            thickness = 1.dp
                        )
                    }
                }

                items(items) { item ->
                    val isSelected = tempSelectedItems.contains(item)

                    WMCheckBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .padding(horizontal = 28.dp),
                        iconModifier = Modifier.size(28.dp),
                        label = itemLabel(item),
                        labelStyle = Typography().bodyLarge,
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            tempSelectedItems = if (checked) {
                                tempSelectedItems + item
                            } else {
                                tempSelectedItems - item
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        WMButton(
            text = "확인",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            onClick = {
                onItemsSelected(tempSelectedItems.toList())
                onDismissRequest()
            }
        )
    }
}
