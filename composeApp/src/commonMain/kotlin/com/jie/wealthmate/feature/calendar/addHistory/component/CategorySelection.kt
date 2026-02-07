package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

@Composable
fun CategorySelectionColumn(
    modifier: Modifier = Modifier,
    categoryItems: List<CategoryVo>,
    selectedCategory: CategoryVo?,
    selectedCategoryTag: CategoryTagVo?,
) {
    Column(modifier = modifier) {
        WMText(
            text = "카테고리",
            style = Typography().titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .heightIn(max = 200.dp)
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 6.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
            ) {
                items(
                    count = categoryItems.size,
                    key = { index -> categoryItems[index].id }
                ) {
                    val category = categoryItems[it]
                    CategorySelectionItem(
                        modifier = Modifier
                            .height(70.dp)
                            .width(88.dp)
                            .noRippleClickable { },
                        category = category,
                        isSelectedCategory = category.id == selectedCategory?.id,
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(ColorGray.Gray_100)
            )

            FlowRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedCategory?.tags?.forEach { item ->
                    val isSelectedTag = selectedCategoryTag?.id == item.id

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = if (isSelectedTag) ColorPrimary.Primary_500 else ColorGray.White)
                            .clickable { }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = item.label,
                            style = Typography().labelMedium.copy(color = if (isSelectedTag) ColorGray.White else ColorGray.Gray_700)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySelectionAllTagColumn(
    modifier: Modifier = Modifier,
    categoryItems: List<CategoryVo>,
    selectedCategory: CategoryVo?,
    selectedCategoryTag: CategoryTagVo?,
) {
    Column(modifier = modifier) {
        WMText(
            text = "카테고리",
            style = Typography().titleSmall.copy(fontWeight = FontWeight.SemiBold)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .heightIn(max = 200.dp)
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 6.dp,
                top = 12.dp,
                bottom = 12.dp
            )
        ) {
            items(
                count = categoryItems.size,
                key = { index -> categoryItems[index].id }
            ) {
                var isSingleLine by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth().height(70.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val category = categoryItems[it]
                    CategorySelectionItem(
                        modifier = Modifier
                            .height(70.dp)
                            .width(88.dp)
                            .noRippleClickable { },
                        category = category,
                        isSelectedCategory = category.id == selectedCategory?.id,
                    )

                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(70.dp)
                            .background(ColorGray.Gray_100)
                    )

                    FlowRow(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp)
                            .padding(bottom = if (isSingleLine) 20.dp else 0.dp)
                            .onGloballyPositioned { coordinates ->
                                // FlowRow의 높이로 줄 수 추정
                                println(coordinates.size.height.dp)
                                val itemHeight = 65.dp
                                isSingleLine = coordinates.size.height.dp <= itemHeight
                            },
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        category.tags.forEach { item ->
                            val isSelectedTag = selectedCategoryTag?.id == item.id

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(color = if (isSelectedTag) ColorPrimary.Primary_500 else ColorGray.White)
                                    .clickable { }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .align(Alignment.CenterVertically),
                                contentAlignment = Alignment.Center
                            ) {
                                WMText(
                                    text = item.label,
                                    style = Typography().labelMedium.copy(color = if (isSelectedTag) ColorGray.White else ColorGray.Gray_700)
                                )
                            }
                        }
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
    Column(modifier = modifier) {
        title?.let {
            WMText(
                text = it,
                style = Typography().titleSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
                .heightIn(max = 200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorGray.Gray_50)
        ) {
            if (categoryItems.isEmpty()) {
                WMText(
                    text = "카테고리가 없습니다.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = Typography().bodyMedium.copy(color = ColorGray.Gray_300)
                )
                return
            }

            LazyRow(
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
            ) {
                items(
                    count = categoryItems.size,
                    key = { index -> categoryItems[index].id }
                ) {
                    val category = categoryItems[it]
                    CategorySelectionItem(
                        modifier = Modifier
                            .size(70.dp)
                            .noRippleClickable { onCategoryClick(category) },
                        category = category,
                        isSelectedCategory = category.id == selectedCategory?.id,
                    )
                }
            }



            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(ColorGray.Gray_100)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedLargeCategory != selectedCategory?.largeCategory) return@LazyRow

                items(
                    count = selectedCategory.tags.size,
                    key = { index -> selectedCategory.tags[index].id.default() }
                ) {
                    val tag = selectedCategory.tags[it]
                    val isSelectedTag = selectedCategoryTag?.id == tag.id

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = if (isSelectedTag) ColorPrimary.Primary_500 else ColorGray.White)
                            .clickable { onCategoryTagClick(tag) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = tag.label,
                            style = Typography().labelMedium.copy(color = if (isSelectedTag) ColorGray.White else ColorGray.Gray_700)
                        )
                    }
                }
            }
        }
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
        Box(modifier = Modifier.width(60.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(category.largeCategory.backgroundColor)
                    .size(40.dp)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                WMText(
                    text = category.icon,
                    style = Typography().titleLarge
                )
            }
            if (category.isFixed) {
                Icon(
                    painter = painterResource(Res.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = ColorRed.Red_300,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopStart)
                )
            }

            if (isSelectedCategory) {
                Icon(
                    painter = painterResource(Res.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = ColorPrimary.Primary_500,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        WMText(
            text = category.middleLabel,
            style = Typography().titleSmall,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}