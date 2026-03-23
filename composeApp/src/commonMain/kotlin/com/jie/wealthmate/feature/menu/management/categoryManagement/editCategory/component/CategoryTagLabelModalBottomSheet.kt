package com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline

@Composable
fun CategoryTagLabelModalBottomSheet(
    isKeyboardOpen: Boolean,
    tagLabel: TextFieldValue,
    selectedTagLabel: String,
    onTagLabelChange: (TextFieldValue) -> Unit,
    onRemoveClick: () -> Unit = {},
    onModifyClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isKeyboardOpen) {
        if (isKeyboardOpen) {
            focusRequester.requestFocus()
        }
    }

    WMModalBottomSheet(
        title = "$selectedTagLabel 상세 태그 수정",
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier.padding(bottom = Padding.BackgroundBottom)
        ) {
            Row(
                modifier = Modifier.padding(start = Padding.BackgroundHorizontal, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WMTextField(
                    value = tagLabel,
                    onValueChange = onTagLabelChange,
                    maxLength = 15,
                    label = "상세 태그 이름",
                    placeholder = selectedTagLabel,
                    isCount = true,
                    textFieldModifier = Modifier.focusRequester(focusRequester),
                    modifier = Modifier.weight(1f)
                )

                WMIconButton(
                    iconRes = Res.drawable.ic_delete_outline,
                    onClick = {
                        onRemoveClick()
                        onDismissRequest()
                    }
                )
            }

            WMButton(
                text = "수정",
                buttonStyle = ButtonStyle.FILLED,
                buttonSize = ButtonSize.LARGE,
                enabled = tagLabel.text.isNotBlank(),
                modifier = Modifier
                    .padding(top = Padding.SpacerXXS)
                    .padding(horizontal = Padding.BackgroundHorizontal)
                    .fillMaxWidth(),
                onClick = {
                    onModifyClick()
                    onDismissRequest()
                }
            )
        }
    }
}

@Composable
@Preview
private fun CategoryTagLabelModalBottomSheetPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .background(ColorSetting.Info)
                .padding(Padding.SpacerM)
        ) {
            CategoryTagLabelModalBottomSheet(
                isKeyboardOpen = false,
                tagLabel = TextFieldValue("수정할 태그"),
                selectedTagLabel = "기존 태그",
                onTagLabelChange = {},
                onRemoveClick = {},
                onModifyClick = {},
                onDismissRequest = {}
            )
        }
    }
}
