package com.jie.wealthmate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            items(items) { item ->
                val isSelected = item == tempSelectedItem

                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ColorPrimary.Primary_200 else ColorGray.White)
                        .clickable { tempSelectedItem = item },
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

        WMButton(
            text = "확인",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            onClick = {
                tempSelectedItem?.let { onItemSelected(it) }
                onDismissRequest()
            }
        )
    }
}
