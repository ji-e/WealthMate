package com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect

data class AddPaymentMethodUiState(
    val isChangedData: Boolean = false,
    val label: TextFieldValue = TextFieldValue(""),
    val groupLabel: String = ""
) : BaseUiState

sealed class AddPaymentMethodUiSideEffect : UiSideEffect {
    data object OnSuccessSave : AddPaymentMethodUiSideEffect()
}
