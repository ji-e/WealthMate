package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue

@Composable
fun AmountTextField(
    modifier: Modifier = Modifier,
    amount: TextFieldValue,
    onUpdateAmount: (TextFieldValue) -> Unit,
) {
    Row(modifier = modifier) {
        WMTextField(
            label = "금액",
            value = amount,
            onValueChange = {
                onUpdateAmount(it.toIntegerTextFieldValue())
            },
            isRequire = true,
            maxLength = 10,
            placeholder = "금액을 입력해 주세요.",
            suffix = {
                WMText(
                    text = "원",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            visualTransformation = rememberIntegerVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            isSupport = false
        )
    }
}