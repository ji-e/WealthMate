package com.jie.wealthmate.feature.menu.paymentMethodManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.paymentMethodManagement.component.PaymentMethodItemData

data class PaymentMethodManagementUiState(
    val paymentMethodItems: List<PaymentMethodItemData> = emptyList(),
) : BaseUiState
