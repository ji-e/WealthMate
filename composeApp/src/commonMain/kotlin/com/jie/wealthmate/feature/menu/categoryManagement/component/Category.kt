package com.jie.wealthmate.feature.menu.categoryManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.reorderable.ReorderableItem
import com.jie.wealthmate.component.reorderable.ReorderableLazyListState
import com.jie.wealthmate.component.reorderable.detectReorderAfterLongPress
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_drag_handle
import wealthmate.composeapp.generated.resources.ic_push_pin

@Composable
fun ColumnScope.Category(
    modifier: Modifier = Modifier,
    listState: ReorderableLazyListState,
    categoryItems: List<CategoryItemData>,
    isDragging: Boolean = false,
    onIsDraggingChange: (Boolean) -> Unit = {},
    onItemClick: (CategoryItemData) -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current

    LazyColumn(
        state = listState.listState,
        modifier = modifier
            .fillMaxSize()
            .reorderable(listState),
    ) {
        items(
            count = categoryItems.size,
            key = { index -> categoryItems[index].id }
        ) { index ->
            val category = categoryItems[index]
            ReorderableItem(
                state = listState,
                key = category.id,
            ) {
                if (isDragging.not()) {
                    onIsDraggingChange(it)
                }

                if (it) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                CategoryItem(
                    data = category,
                    modifier = Modifier.then(
                        if (it) {
                            Modifier
                                .padding(horizontal = 12.dp)
                                .dropShadow(
                                    shape = RoundedCornerShape(4.dp),
                                    shadow = Shadow(
                                        radius = 10.dp,
                                        spread = 10.dp,
                                        color = ColorPrimary.Primary_200,
                                        offset = DpOffset(x = 4.dp, 4.dp)
                                    )
                                )
                                .clip(RoundedCornerShape(4.dp))
                        } else {
                            Modifier.clickable(isDragging.not()) {
                                onItemClick(category)
                            }
                        }
                    ),
                    onDragHandle = Modifier.detectReorderAfterLongPress(listState),
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    data: CategoryItemData,
    modifier: Modifier = Modifier,
    onDragHandle: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White)
            .padding(
                vertical = 14.dp,
                horizontal = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(52.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(data.largeCategory.backgroundColor)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = data.icon,
                    style = Typography().bodyLarge.copy(fontSize = 28.sp)
                )
            }
            if (data.isFixed) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .padding()
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                )
            }
        }


        WMText(
            text = data.label,
            style = Typography().bodyLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            maxLines = 1,
        )

        Icon(
            painter = painterResource(Res.drawable.ic_drag_handle),
            contentDescription = "이동",
            tint = ColorGray.Gray_300,
            modifier = onDragHandle.size(28.dp)
        )
    }

}

@Composable
@Preview(showBackground = true)
private fun CategoryItemPreview() {
    WMTheme {
        CategoryItem(
            CategoryItemData(
                id = "0",
                icon = CategoryIconEnum.CATEGORY_U1F9D0.text,
                label = "급여",
                sort = 1,
                largeCategory = LargeCategoryEnum.EXPENSES
            )
        )
    }
}