package com.jie.wealthmate.feature.calendar.component.addHistory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.CategoryTagVo
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun CategoryTextField(
    modifier: Modifier = Modifier,
    selectedLargeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    categoryItemData: CategoryItemData? = null,
    tagLabelItems: List<CategoryTagVo> = emptyList(),
    selectedTagLabel: CategoryTagVo? = null,
    onCategoryClick: () -> Unit = {},
    onTagLabelClick: (CategoryTagVo) -> Unit = {},
) {
    WMTextField(
        value = categoryItemData?.label ?: selectedLargeCategoryEnum.tempMiddleCategoryLabel,
        onValueChange = {},
        modifier = modifier,
        label = "카테고리",
        readOnly = true,
        isRequire = true,
        onClickReadOnly = onCategoryClick,
        supportingContent = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tagLabelItems.forEach { item ->
                    val isSelected = selectedTagLabel == item
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = if (isSelected) ColorPrimary.Primary_500 else ColorGray.Gray_100)
                            .clickable { onTagLabelClick(item) }
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
@Preview(showBackground = true)
private fun CategoryTextFieldPreview() {
    WMTheme {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CategoryTextField(
                categoryItemData = CategoryItemData(
                    id = "0",
                    icon = CategoryIconEnum.CATEGORY_U1F9D0.text,
                    label = "급여",
                    sort = 1,
                    largeCategory = LargeCategoryEnum.EXPENSES
                ),
                tagLabelItems = listOf(
                    CategoryTagVo(
                        id = "0",
                        label = "외식"
                    ),
                    CategoryTagVo(
                        id = "1",
                        label = "주책 청약"
                    )
                ),
                selectedTagLabel = CategoryTagVo(
                    id = "0",
                    label = "외식"
                )
            )
        }
    }
}

