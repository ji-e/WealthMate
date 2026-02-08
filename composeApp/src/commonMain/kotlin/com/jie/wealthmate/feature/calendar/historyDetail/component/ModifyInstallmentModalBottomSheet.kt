package com.jie.wealthmate.feature.calendar.historyDetail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.utils.default

@Composable
fun ModifyInstallmentModalBottomSheet(
    totalAmount: Long?,
    totalCount: Long?,
    onConfirmClick: (totalAmount: String, totalCount: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    WMModalBottomSheet(
        title = "할부 내역 수정",
        onDismissRequest = onDismissRequest,
    ) {
        var totalAmount by remember {
            mutableStateOf(
                TextFieldValue(totalAmount.default().toString())
            )
        }
        var totalCount by remember {
            mutableStateOf(
                TextFieldValue(totalCount.default().toString())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
        ) {
            WMTextField(
                value = totalAmount,
                onValueChange = {
                    totalAmount = it.toIntegerTextFieldValue()
                },
                label = "총 결제 금액",
                suffix = {
                    WMText(
                        text = "원",
                        style = androidx.compose.material3.Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                placeholder = "총 결제 금액을 입력해 주세요.",
                visualTransformation = rememberIntegerVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                maxLength = 10,
            )

            WMTextField(
                value = totalCount,
                onValueChange = { totalCount = it },
                label = "할부 기간",
                maxLength = 2,
                placeholder = "할부 기간을 입력해주세요.",
                suffix = {
                    WMText(
                        text = "개월",
                        style = Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            WMButton(
                text = "확인",
                buttonStyle = ButtonStyle.FILLED,
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismissRequest()
                    onConfirmClick(totalAmount.text, totalCount.text)
                }
            )
        }
    }
}