package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.LabelText
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryVo
import kotlinx.collections.immutable.persistentListOf

@Composable
fun Category(
    modifier: Modifier = Modifier,
    category: CategoryVo?,
    categoryTag: CategoryTagVo?,
    onCategoryClick: () -> Unit,
) {
    Column(modifier = modifier) {
        LabelText(text = "카테고리")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Padding.ContainerVertical)
                .height(60.dp)
                .clip(Shapes.medium)
                .background(ColorSetting.EmptyBackground)
                .clickable { onCategoryClick() }
                .padding(start = Padding.SpacerXXS, end = Padding.SpacerS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (category == null) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = "카테고리 없음",
                        style = MaterialTheme.typography.titleSmall,
                        color = ColorSetting.DisabledContent
                    )
                }
            } else {
                EmojiIcon(
                    icon = category.icon,
                    color = category.largeCategory.backgroundColor,
                    isFixedUsed = true,
                    isFixed = category.isFixed
                )

                WMText(
                    text = category.middleLabel,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    modifier = Modifier.padding(start = Padding.SpacerXS)
                )

                categoryTag?.let {
                    WMText(
                        text = " > ${it.label}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CategoryPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.BackgroundHorizontal)
        ) {
            Category(
                category = CategoryVo(
                    id = "1",
                    icon = "🍔",
                    largeCategory = LargeCategoryEnum.EXPENSES,
                    middleLabel = "식비",
                    sort = 1,
                    isFixed = false,
                    tags = persistentListOf()
                ),
                categoryTag = CategoryTagVo(id = "1", label = "외식"),
                onCategoryClick = {}
            )

            Category(
                modifier = Modifier.padding(top = Padding.SpacerS),
                category = null,
                categoryTag = null,
                onCategoryClick = {}
            )
        }
    }
}
