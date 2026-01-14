package com.jie.wealthmate.feature.menu.categorySetting.addCategory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.WMTextField
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.CategoryTagVo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close_circle

@Composable
fun CategoryTag(
    modifier: Modifier = Modifier,
    largeCategory: LargeCategoryEnum,
    tagLabel: TextFieldValue,
    tagLabelItems: List<CategoryTagVo> = emptyList(),
    onValueChange: (TextFieldValue) -> Unit,
    onChipAdd: () -> Unit = {},
    onChipClick: (CategoryTagVo) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        WMTextField(
            value = tagLabel,
            onValueChange = onValueChange,
            modifier = Modifier.padding(horizontal = 4.dp),
            maxLength = 15,
            label = "상세 태그 이름",
            placeholder = largeCategory.tempTagLabel,
            supportingText = "15자 이내로 입력해 주세요.",
            enabled = tagLabelItems.size <= 10,
            isCount = true,
            keyboardActions = KeyboardActions(
                onDone = { onChipAdd() }
            )
        )

        CategoryTagItem(
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 20.dp),
            chipItems = tagLabelItems,
            onChipClick = onChipClick
        )
    }
}

@Composable
fun CategoryTagItem(
    modifier: Modifier = Modifier,
    chipItems: List<CategoryTagVo>,
    onChipClick: (CategoryTagVo) -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chipItems.forEach { item ->
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = ColorGray.Gray_100)
                    .clickable { onChipClick(item) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WMText(
                    text = item.label,
                    style = Typography().labelMedium
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_close_circle),
                    contentDescription = item.label,
                    tint = ColorGray.Gray_400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CategoryTagPreView() {
    WMTheme {
        CategoryTag(
            tagLabel = TextFieldValue(""),
            onValueChange = {},
            largeCategory = LargeCategoryEnum.INCOME,
        )
    }
}