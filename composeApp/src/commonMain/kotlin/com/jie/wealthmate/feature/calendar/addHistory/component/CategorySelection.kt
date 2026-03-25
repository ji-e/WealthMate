package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.EmojiIconSize
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMHorizontalDivider
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_check_circle

private object CategorySelectionDefaults {
    val ContainerShape = RoundedCornerShape(8.dp)
    val ContainerBackground = ColorGray.Gray_50
    val DividerColor = ColorGray.Gray_100
    val CategoryItemSize = 70.dp
    val CategoryIconSize = 40.dp
    val BadgeIconSize = 22.dp
    val MaxContainerHeight = 200.dp
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySelectionColumn(
    modifier: Modifier = Modifier,
    title: String = "카테고리",
    categoryItems: List<CategoryVo>,
    selectedCategory: CategoryVo?,
    selectedCategoryTag: CategoryTagVo?,
    onCategoryClick: (CategoryVo) -> Unit = {},
    onCategoryTagClick: (CategoryTagVo) -> Unit = {},
) {
    Column(modifier = modifier) {
        WMText(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = CategorySelectionDefaults.MaxContainerHeight)
                .padding(top = 12.dp)
                .clip(CategorySelectionDefaults.ContainerShape)
                .background(CategorySelectionDefaults.ContainerBackground)
        ) {
            if (categoryItems.isEmpty()) {
                EmptyCategoryMessage(modifier = Modifier.fillMaxWidth().padding(32.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.width(96.dp),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(categoryItems, key = { it.id }) { category ->
                        CategorySelectionItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(CategorySelectionDefaults.CategoryItemSize)
                                .noRippleClickable { onCategoryClick(category) },
                            category = category,
                            isSelectedCategory = category.id == selectedCategory?.id,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(CategorySelectionDefaults.DividerColor)
                )

                FlowRow(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedCategory?.tags?.forEach { item ->
                        CategoryTagItem(
                            tag = item,
                            isSelected = selectedCategoryTag?.id == item.id,
                            onClick = { onCategoryTagClick(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySelectionRow(
    modifier: Modifier = Modifier,
    title: String? = "카테고리",
    categoryItems: List<CategoryVo>,
    selectedLargeCategory: LargeCategoryEnum,
    selectedCategory: CategoryVo?,
    selectedCategoryTag: CategoryTagVo?,
    onCategoryClick: (CategoryVo) -> Unit,
    onCategoryTagClick: (CategoryTagVo) -> Unit,
) {
    val categoryGridState = rememberLazyGridState()
    val density = LocalDensity.current

    LaunchedEffect(selectedCategory?.id) {
        val index = categoryItems.indexOfFirst { it.id == selectedCategory?.id }
        if (index >= 0) {
            val layoutInfo = categoryGridState.layoutInfo
            val viewportHeight = layoutInfo.viewportSize.height
            if (viewportHeight > 0) {
                val itemHeightPx =
                    with(density) { CategorySelectionDefaults.CategoryItemSize.roundToPx() }
                val centerOffset = (viewportHeight - itemHeightPx) / 2
                categoryGridState.animateScrollToItem(index, -centerOffset)
            }
        }
    }

    Column(modifier = modifier) {
        title?.let {
            LabelText(
                text = it,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CategorySelectionDefaults.ContainerShape)
                .background(CategorySelectionDefaults.ContainerBackground)
        ) {
            if (categoryItems.isEmpty()) {
                EmptyCategoryMessage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Padding.SpacerL)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    state = categoryGridState,
                    modifier = Modifier.heightIn(max = CategorySelectionDefaults.MaxContainerHeight),
                    contentPadding = PaddingValues(
                        vertical = Padding.ContainerVertical,
                        horizontal = Padding.SpacerXS
                    ),
                    horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXXS),
                    verticalArrangement = Arrangement.spacedBy(Padding.SpacerXXS)
                ) {
                    items(categoryItems, key = { it.id }) { category ->
                        CategorySelectionItem(
                            modifier = Modifier
                                .size(CategorySelectionDefaults.CategoryItemSize)
                                .noRippleClickable { onCategoryClick(category) },
                            category = category,
                            isSelectedCategory = category.id == selectedCategory?.id,
                        )
                    }
                }

                val showTags = selectedCategory != null &&
                        selectedLargeCategory == selectedCategory.largeCategory &&
                        selectedCategory.tags.isNotEmpty()


                if (showTags) {
                    WMHorizontalDivider()

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        contentPadding = PaddingValues(horizontal = Padding.ContainerHorizontal),
                        horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(selectedCategory.tags, key = { it.id.default() }) { tag ->
                            CategoryTagItem(
                                tag = tag,
                                isSelected = selectedCategoryTag?.id == tag.id,
                                onClick = { onCategoryTagClick(tag) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCategoryMessage(modifier: Modifier = Modifier) {
    WMText(
        text = "카테고리가 없습니다.",
        modifier = modifier,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = ColorSetting.DisabledContent
    )
}

@Composable
private fun CategoryTagItem(
    tag: CategoryTagVo,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color = if (isSelected) ColorSetting.Primary else ColorGray.White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = tag.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) ColorGray.White else ColorSetting.Default
        )
    }
}

@Composable
private fun CategorySelectionItem(
    modifier: Modifier = Modifier,
    category: CategoryVo,
    isSelectedCategory: Boolean,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .padding(end = EmojiIconSize.MEDIUM.fixedIconSize / 3)
                .width(56.dp),
            contentAlignment = Alignment.Center
        ) {

            EmojiIcon(
                icon = category.icon,
                color = category.largeCategory.backgroundColor,
                isFixed = category.isFixed,
                isFixedUsed = true
            )

            if (isSelectedCategory) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = ColorSetting.Primary,
                    modifier = Modifier
                        .size(CategorySelectionDefaults.BadgeIconSize)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        WMText(
            text = category.middleLabel,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelectedCategory) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = Padding.SpacerXXS),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
