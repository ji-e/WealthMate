package com.jie.wealthmate.feature.menu.management.categoryManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.reorderable.ReorderableItem
import com.jie.wealthmate.component.reorderable.ReorderableLazyListState
import com.jie.wealthmate.component.reorderable.detectReorderAfterLongPress
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_drag_handle
import wealthmate.composeapp.generated.resources.ic_push_pin

@Composable
fun ColumnScope.Category(
    modifier: Modifier = Modifier,
    listState: ReorderableLazyListState,
    categoryItems: List<CategoryVo>,
    isDragging: Boolean = false,
    onIsDraggingChange: (Boolean) -> Unit = {},
    onItemClick: (CategoryVo) -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current

    LazyColumn(
        state = listState.listState,
        modifier = modifier
            .fillMaxSize()
            .reorderable(listState),
        contentPadding = PaddingValues(top  = 12.dp, bottom = Padding.BackgroundBottom)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

        }
        itemsIndexed(categoryItems, { _, item -> item.id }) { _, item ->
            ReorderableItem(
                reorderableState = listState,
                key = item.id,
            ) { dragging ->
                if (isDragging.not() && dragging) {
                    onIsDraggingChange(true)
                }

                if (dragging) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                CategoryItem(
                    data = item,
                    modifier = Modifier.then(
                        if (dragging) {
                            Modifier
                                .padding(horizontal = 20.dp)
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
                            Modifier.clickable(enabled = !isDragging) { onItemClick(item) }
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
    data: CategoryVo,
    modifier: Modifier = Modifier,
    onDragHandle: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography
    val tagText = remember(data.tags) { data.tags.joinToString { it.label } }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorGray.White)
            .padding(start = 20.dp, end = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.width(60.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(data.largeCategory.backgroundColor)
                    .size(40.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = data.icon,
                    style = typography.titleLarge
                )
            }
            if (data.isFixed) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopStart)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            WMText(
                text = data.middleLabel,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
            )
            if (tagText.isNotEmpty()) {
                WMText(
                    text = tagText,
                    style = typography.bodySmall.copy(color = ColorGray.Gray_500),
                    maxLines = 1,
                )
            }
        }

        Icon(
            painter = painterResource(Res.drawable.ic_drag_handle),
            contentDescription = "이동",
            tint = ColorGray.Gray_300,
            modifier = onDragHandle.size(28.dp)
        )
    }
}
