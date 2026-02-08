package com.jie.wealthmate.feature.calendar.addHistory.component

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

@Composable
fun InstallmentModalBottomSheet(
    totalInstallmentCount: Long?,
    onConfirmClick: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var tempInstallmentCount by remember {
        mutableStateOf(TextFieldValue(totalInstallmentCount?.toString() ?: ""))
    }

    WMModalBottomSheet(
        title = "할부 기간",
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
        ) {
            WMTextField(
                value = tempInstallmentCount,
                onValueChange = { tempInstallmentCount = it },
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
            )

            WMButton(
                text = "확인",
                buttonStyle = ButtonStyle.FILLED,
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismissRequest()
                    onConfirmClick(tempInstallmentCount.text.toLongOrNull())
                }
            )
        }
    }
}