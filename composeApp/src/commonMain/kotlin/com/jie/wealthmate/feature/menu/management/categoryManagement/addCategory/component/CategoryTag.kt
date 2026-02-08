package com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.CategoryTagVo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_close_circle2

@Composable
fun CategoryTag(
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    largeCategory: LargeCategoryEnum,
    tagLabel: TextFieldValue,
    trailingIcon: DrawableResource = Res.drawable.ic_close_circle2,
    tagLabelItems: List<CategoryTagVo> = emptyList(),
    onValueChange: (TextFieldValue) -> Unit,
    onChipAdd: (TextFieldValue?) -> Unit = {},
    onChipClick: (CategoryTagVo) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        WMTextField(
            value = tagLabel,
            onValueChange = onValueChange,
            modifier = Modifier
                .focusRequester(remember { FocusRequester() })
                .onFocusChanged { focusState ->
                    if (focusState.isFocused.not()) {
                        onChipAdd(null)
                    }
                },
            textFieldModifier = textFieldModifier,
            maxLength = 15,
            label = "상세 태그 이름",
            placeholder = largeCategory.tempTagLabel,
            enabled = tagLabelItems.size < 10,
            isCount = true,
            keyboardActions = KeyboardActions(
                onDone = { onChipAdd(tagLabel) }
            ),
            supportingContent = {
                CategoryTagItem(
                    modifier = Modifier.padding(top = 8.dp),
                    trailingIcon = trailingIcon,
                    chipItems = tagLabelItems,
                    onChipClick = onChipClick
                )
            }
        )
    }
}

@Composable
fun CategoryTagItem(
    modifier: Modifier = Modifier,
    trailingIcon: DrawableResource,
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
                    .background(color = ColorPrimary.Primary_300)
                    .clickable { onChipClick(item) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WMText(
                    text = item.label,
                    style = Typography().labelMedium
                )

                // 삭제 아이콘
                Icon(
                    painter = painterResource(trailingIcon),
                    contentDescription = item.label,
                    tint = ColorGray.Gray_500,
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