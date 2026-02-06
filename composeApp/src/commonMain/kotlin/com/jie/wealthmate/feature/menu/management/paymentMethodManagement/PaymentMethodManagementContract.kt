package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.component.PaymentMethodItemData

data class PaymentMethodManagementUiState(
    val paymentMethodItems: List<PaymentMethodItemData> = emptyList(),
) : BaseUiState
