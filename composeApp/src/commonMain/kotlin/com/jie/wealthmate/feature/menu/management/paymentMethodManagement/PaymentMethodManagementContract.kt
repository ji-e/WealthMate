package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.vo.PaymentMethodVo

data class PaymentMethodManagementUiState(
    val paymentMethodItems: List<PaymentMethodVo>? = null,
) : BaseUiState
