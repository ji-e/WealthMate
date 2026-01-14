package com.jie.wealthmate.feature.menu.categorySetting.addCategory.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMTextField
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryTagTextField(
    modifier: Modifier = Modifier,
    largeCategory: LargeCategoryEnum,
    tagLabel: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
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
            isCount = true,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun CategoryTagTextFieldPreview() {
    WMTheme {
        CategoryTagTextField(
            tagLabel = TextFieldValue(""),
            onValueChange = {},
            largeCategory = LargeCategoryEnum.INCOME,
        )
    }
}