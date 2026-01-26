package com.jie.wealthmate.feature.menu.paymentMethodManagement.modifyPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData

data class ModifyPaymentMethodUiState(
    val isDataChanged: Boolean = false,
    val label: TextFieldValue = TextFieldValue(""),
    val group: PaymentMethodGroupItemData? = null,
    val groupItems: List<PaymentMethodGroupItemData> = emptyList(),
) : BaseUiState

sealed class ModifyPaymentMethodUiSideEffect : UiSideEffect {
    data object OnSuccessSave : ModifyPaymentMethodUiSideEffect()
}
