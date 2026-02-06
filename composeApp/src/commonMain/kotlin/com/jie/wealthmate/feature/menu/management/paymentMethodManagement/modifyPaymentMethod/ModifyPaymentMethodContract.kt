package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData

data class ModifyPaymentMethodUiState(
    val isDataChanged: Boolean = false,
    val label: TextFieldValue = TextFieldValue(""),
    val group: PaymentMethodGroupItemData? = null,
    val sort: Long = 0L,
) : BaseUiState

sealed class ModifyPaymentMethodUiSideEffect : UiSideEffect {
    data object OnSuccess : ModifyPaymentMethodUiSideEffect()
}
