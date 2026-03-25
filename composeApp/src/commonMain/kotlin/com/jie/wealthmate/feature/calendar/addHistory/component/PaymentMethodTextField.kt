package com.jie.wealthmate.feature.calendar.addHistory.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.PaymentMethodVo


@Composable
fun PaymentMethodTextField(
    modifier: Modifier = Modifier,
    selectedLargeCategory: LargeCategoryEnum = LargeCategoryEnum.EXPENSES,
    selectedPaymentMethod: PaymentMethodVo?,
    placeholder: String = "을 선택해 주세요.",
    onPaymentMethodClick: () -> Unit = {},
) {
    val label = if (selectedLargeCategory == LargeCategoryEnum.EXPENSES) "결제수단" else "자산"

    Row(modifier = modifier) {
        WMTextField(
            value = selectedPaymentMethod?.label.default(),
            onValueChange = {},
            label = label,
            readOnly = true,
            placeholder = "$label$placeholder",
            onReadOnlyClick = onPaymentMethodClick,
            isSupport = false
        )
    }
}

