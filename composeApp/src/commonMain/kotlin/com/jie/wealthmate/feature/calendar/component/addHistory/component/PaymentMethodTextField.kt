package com.jie.wealthmate.feature.calendar.component.addHistory.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.feature.menu.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun PaymentMethodTextField(
    modifier: Modifier = Modifier,
    selectedLargeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    onPaymentMethodClick: () -> Unit = {},
) {
    WMTextField(
        value = "현금",
        onValueChange = {},
        modifier = modifier,
        label = if (selectedLargeCategoryEnum == LargeCategoryEnum.EXPENSES) "결제수단" else "자산",
        readOnly = true,
        onClickReadOnly = onPaymentMethodClick,
    )
}

@Composable
@Preview(showBackground = true)
private fun PaymentMethodTextFieldPreview() {
    WMTheme {
        PaymentMethodTextField()
    }
}

