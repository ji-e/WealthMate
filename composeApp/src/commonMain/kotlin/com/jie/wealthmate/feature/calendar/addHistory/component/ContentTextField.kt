package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.component.textField.WMTextField

@Composable
fun ContentTextField(
    modifier: Modifier = Modifier,
    content: TextFieldValue,
    onUpdateContent: (TextFieldValue) -> Unit,
) {
    Row(modifier = modifier) {
        WMTextField(
            label = "내용",
            value = content,
            onValueChange = onUpdateContent,
            maxLength = 20,
            placeholder = "내용을 입력해 주세요.",
            isSupport = false
        )
    }
}