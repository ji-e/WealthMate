package com.jie.wealthmate.feature.menu.management.categoryManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.reorderable.ReorderableItem
import com.jie.wealthmate.component.reorderable.ReorderableLazyListState
import com.jie.wealthmate.component.reorderable.detectReorderAfterLongPress
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_drag_handle

@Composable
fun Category(
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
        contentPadding = PaddingValues(
            top = Padding.ContainerVertical,
            bottom = Padding.BackgroundBottom
        )
    ) {
        item {
            Spacer(modifier = Modifier.height(Padding.SpacerXS))
        }

        items(
            items = categoryItems,
            key = { it.id }
        ) { item ->
            ReorderableItem(
                reorderableState = listState,
                key = item.id,
            ) { dragging ->
                // 드래그 시작 시점에 한 번만 실행되도록 처리
                LaunchedEffect(dragging) {
                    if (dragging) {
                        onIsDraggingChange(true)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }

                // Modifier 메모이제이션을 통해 불필요한 객체 생성 방지
                val itemModifier = remember(dragging, isDragging) {
                    if (dragging) {
                        Modifier
                            .padding(horizontal = Padding.ContainerHorizontal)
                            .dropShadow(
                                shape = Shapes.small,
                                shadow = Shadow(
                                    radius = 10.dp,
                                    spread = 10.dp,
                                    color = ColorPrimary.Primary_200,
                                    offset = DpOffset(x = 4.dp, 4.dp)
                                )
                            )
                            .clip(Shapes.small)
                    } else {
                        Modifier.clickable(enabled = isDragging.not()) { onItemClick(item) }
                    }
                }

                CategoryItem(
                    data = item,
                    modifier = itemModifier,
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
            .padding(start = 20.dp, end = Padding.BackgroundHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EmojiIcon(
            icon = data.icon,
            color = data.largeCategory.backgroundColor,
            isFixedUsed = true,
            isFixed = data.isFixed
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Padding.SpacerXS)
        ) {
            WMText(
                text = data.middleLabel,
                style = typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
            if (tagText.isNotEmpty()) {
                WMText(
                    text = tagText,
                    style = typography.bodySmall,
                    color = ColorSetting.Info,
                    maxLines = 1,
                )
            }
        }

        Icon(
            painter = painterResource(Res.drawable.ic_drag_handle),
            contentDescription = "이동",
            tint = ColorSetting.DisabledContent,
            modifier = onDragHandle.size(28.dp)
        )
    }
}

@Preview
@Composable
private fun CategoryPreview() {
    val listState = rememberReorderableLazyListState(onMove = { _, _ -> })
    val mockCategories = persistentListOf(
        CategoryVo(
            id = "1",
            icon = "🍔",
            largeCategory = LargeCategoryEnum.EXPENSES,
            middleLabel = "식비",
            sort = 1,
            isFixed = true,
            tags = persistentListOf(
                CategoryTagVo(id = "1", label = "외식"),
                CategoryTagVo(id = "2", label = "배달")
            )
        ),
        CategoryVo(
            id = "2",
            icon = "🏠",
            largeCategory = LargeCategoryEnum.EXPENSES,
            middleLabel = "주거",
            sort = 2,
            isFixed = false,
            tags = persistentListOf(CategoryTagVo(id = "3", label = "월세"))
        ),
        CategoryVo(
            id = "3",
            icon = "🚌",
            largeCategory = LargeCategoryEnum.EXPENSES,
            middleLabel = "교통",
            sort = 3,
            isFixed = false,
            tags = persistentListOf()
        )
    )

    WMTheme {
        Category(
            listState = listState,
            categoryItems = mockCategories
        )
    }
}
