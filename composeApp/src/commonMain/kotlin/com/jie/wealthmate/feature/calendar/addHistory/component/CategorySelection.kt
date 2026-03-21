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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_check_circle
import wealthmate.composeapp.generated.resources.ic_push_pin

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
    val categoryLazyListState = rememberLazyListState()
    val density = LocalDensity.current

    LaunchedEffect(selectedCategory?.id) {
        val index = categoryItems.indexOfFirst { it.id == selectedCategory?.id }
        if (index >= 0) {
            val layoutInfo = categoryLazyListState.layoutInfo
            val viewportWidth = layoutInfo.viewportSize.width
            if (viewportWidth > 0) {
                val itemWidthPx =
                    with(density) { CategorySelectionDefaults.CategoryItemSize.roundToPx() }
                val centerOffset = (viewportWidth - itemWidthPx) / 2
                categoryLazyListState.animateScrollToItem(index, -centerOffset)
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
                EmptyCategoryMessage(modifier = Modifier.fillMaxWidth().padding(32.dp))
            } else {
                LazyRow(
                    state = categoryLazyListState,
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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

//                if (showTags) {
                    Spacer(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(CategorySelectionDefaults.DividerColor)
                    )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showTags) {
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
        color = ColorGray.Gray_300
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
            .background(color = if (isSelected) ColorPrimary.Primary_500 else ColorGray.White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        WMText(
            text = tag.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) ColorGray.White else ColorGray.Gray_700
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
            modifier = Modifier.width(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(CategorySelectionDefaults.CategoryIconSize)
                    .clip(CircleShape)
                    .background(category.largeCategory.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = category.icon,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (category.isFixed) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .size(CategorySelectionDefaults.BadgeIconSize)
                        .align(Alignment.TopStart)
                )
            }

            if (isSelectedCategory) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = ColorPrimary.Primary_500,
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
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
