package com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component

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
import androidx.compose.material3.MaterialTheme
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
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
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
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier,
    ) {
        WMTextField(
            value = tagLabel,
            onValueChange = onValueChange,
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
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
                    modifier = Modifier.padding(top = Padding.SpacerXS),
                    largeCategory = largeCategory,
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
    largeCategory: LargeCategoryEnum,
    trailingIcon: DrawableResource,
    chipItems: List<CategoryTagVo>,
    onChipClick: (CategoryTagVo) -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXXS),
        verticalArrangement = Arrangement.spacedBy(Padding.SpacerXS)
    ) {
        chipItems.forEach { item ->
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = largeCategory.backgroundColor)
                    .clickable { onChipClick(item) }
                    .padding(horizontal = Padding.SpacerXS, vertical = Padding.SpacerXXS),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXXS)
            ) {
                WMText(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium
                )

                Icon(
                    painter = painterResource(trailingIcon),
                    contentDescription = "삭제",
                    tint = ColorSetting.Info,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
@Preview
private fun CategoryTagPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(Padding.SpacerM),
            verticalArrangement = Arrangement.spacedBy(Padding.SpacerL)
        ) {
            // 수입 태그 예시
            CategoryTag(
                tagLabel = TextFieldValue(""),
                onValueChange = {},
                largeCategory = LargeCategoryEnum.INCOME,
                tagLabelItems = listOf(
                    CategoryTagVo(label = "상여금"),
                    CategoryTagVo(label = "성과급"),
                    CategoryTagVo(label = "용돈")
                )
            )

            // 지출 태그 예시
            CategoryTag(
                tagLabel = TextFieldValue("점심식사"),
                onValueChange = {},
                largeCategory = LargeCategoryEnum.EXPENSES,
                tagLabelItems = listOf(
                    CategoryTagVo(label = "외식"),
                    CategoryTagVo(label = "커피"),
                    CategoryTagVo(label = "배달음식")
                )
            )
        }
    }
}
