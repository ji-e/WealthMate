package com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData

data class AddPaymentMethodUiState(
    val label: TextFieldValue = TextFieldValue(""),
    val group: PaymentMethodGroupItemData? = null,
    val groupItems: List<PaymentMethodGroupItemData> = emptyList(),
) : BaseUiState

sealed class AddPaymentMethodUiSideEffect : UiSideEffect {
    data object OnSuccessSave : AddPaymentMethodUiSideEffect()
}
