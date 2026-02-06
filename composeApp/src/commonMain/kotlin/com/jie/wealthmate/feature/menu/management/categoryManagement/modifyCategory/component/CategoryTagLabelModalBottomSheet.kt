package com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.theme.ColorRed


@Composable
fun CategoryTagLabelModalBottomSheet(
    isKeyboardOpen: Boolean,
    tagLabel: String,
    selectedTagLabel: String,
    onTagLabelChange: (String) -> Unit,
    onRemoveClick: () -> Unit = {},
    onModifyClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (isKeyboardOpen) {
            focusRequester.requestFocus()
        }
    }

    WMModalBottomSheet(
        title = "$selectedTagLabel 상세 태그 수정",
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            WMTextField(
                value = tagLabel,
                onValueChange = onTagLabelChange,
                maxLength = 15,
                label = "상세 태그 이름",
                placeholder = selectedTagLabel,
                supportingText = "15자 이내로 입력해 주세요.",
                isCount = true,
                textFieldModifier = Modifier.focusRequester(focusRequester)
            )

            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WMButton(
                    text = "삭제",
                    buttonStyle = ButtonStyle.TONAL,
                    buttonSize = ButtonSize.LARGE,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ColorRed.Red_50,
                        contentColor = ColorRed.Red_300
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onRemoveClick()
                        onDismissRequest()
                    },
                )

                WMButton(
                    text = "수정",
                    buttonStyle = ButtonStyle.FILLED,
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier.weight(4f),
                    onClick = { onModifyClick() }
                )
            }
        }
    }
}