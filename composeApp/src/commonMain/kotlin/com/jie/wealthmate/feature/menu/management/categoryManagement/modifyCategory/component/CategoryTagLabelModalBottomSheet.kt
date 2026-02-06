package com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.component

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
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.textField.WMTextField
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete


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
            modifier = Modifier.padding(bottom = 20.dp)
        ) {


            Row(
                modifier = Modifier.padding(start = 28.dp, end = 12.dp),
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
                    iconRes = Res.drawable.ic_delete,
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
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth(),
                onClick = { onModifyClick() }
            )
        }
    }
}