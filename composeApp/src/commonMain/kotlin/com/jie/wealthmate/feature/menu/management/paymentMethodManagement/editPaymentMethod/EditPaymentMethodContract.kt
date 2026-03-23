package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.vo.PaymentMethodVo

data class EditPaymentMethodUiState(
    val isDataChanged: Boolean = false,
    val paymentMethodId: String? = null,
    val label: TextFieldValue = TextFieldValue(""),
    val group: PaymentMethodGroupItemData? = null,
    val sort: Long = 0L,
    val paymentMethodItems: List<PaymentMethodVo> = emptyList(),
) : BaseUiState

sealed class EditPaymentMethodUiSideEffect : UiSideEffect {
    data object OnSuccess : EditPaymentMethodUiSideEffect()
}
