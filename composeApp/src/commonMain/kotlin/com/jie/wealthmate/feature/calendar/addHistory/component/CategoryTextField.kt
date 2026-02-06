package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_check_circle
import wealthmate.composeapp.generated.resources.ic_push_pin


@Composable
fun CategoryTextField(
    modifier: Modifier = Modifier,
    selectedLargeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    selectedCategory: CategoryVo? = null,
    selectedCategoryTag: CategoryTagVo? = null,
    onCategoryClick: () -> Unit = {},
    onCategoryTagClick: (CategoryTagVo) -> Unit = {},
) {
    WMTextField(
        value = selectedCategory?.middleLabel.default(),
        onValueChange = {},
        modifier = modifier,
        label = "카테고리",
        readOnly = true,
        onReadOnlyClick = onCategoryClick,
        placeholder = selectedLargeCategoryEnum.tempMiddleCategoryLabel,
        supportingContent = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedCategory?.tags?.forEach { item ->
                    val isSelected = selectedCategoryTag?.id == item.id
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = if (isSelected) ColorPrimary.Primary_500 else ColorGray.Gray_50)
                            .clickable { onCategoryTagClick(item) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        WMText(
                            text = item.label,
                            style = Typography().labelMedium.copy(color = if (isSelected) ColorGray.White else ColorGray.Gray_700)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CategorySelectModalBottomSheet(
    categoryItems: List<CategoryVo>,
    selectedCategory: CategoryVo? = null,
    onDismissRequest: () -> Unit,
) {
    WMModalBottomSheet(
        title = "카테고리 선택",
        onDismissRequest = onDismissRequest,
    ) {
        LazyColumn(modifier = Modifier.height(300.dp)) {
            items(
                count = categoryItems.size,
                key = { index -> categoryItems[index].id }
            ) {
                val category = categoryItems[it]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(ColorGray.White)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.width(60.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(category.largeCategory.backgroundColor)
                                .size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            WMText(
                                text = category.icon,
                                style = Typography().bodyLarge.copy(fontSize = 20.sp)
                            )
                        }
                        if (category.isFixed) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_push_pin),
                                contentDescription = null,
                                tint = ColorRed.Red_300,
                                modifier = Modifier
                                    .padding(start = 24.dp)
                                    .size(24.dp)
                                    .align(Alignment.TopStart)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        WMText(
                            text = category.middleLabel,
                            style = Typography().bodyLarge.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1,
                        )
                        WMText(
                            text = category.tags.joinToString(", ") { tag -> tag.label },
                            style = Typography().labelMedium.copy(color = ColorGray.Gray_500),
                            maxLines = 1,
                        )
                    }

                    if (selectedCategory?.id == category.id) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_check_circle),
                            contentDescription = null,
                            tint = ColorPrimary.Primary_500,
                            modifier = Modifier
                                .padding()
                                .size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
