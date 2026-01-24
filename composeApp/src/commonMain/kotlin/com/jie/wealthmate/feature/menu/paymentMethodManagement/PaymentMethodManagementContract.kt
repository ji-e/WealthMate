package com.jie.wealthmate.feature.menu.paymentMethodManagement

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData

data class PaymentMethodManagementUiState(
    val paymentMethodItems: List<CategoryItemData> = emptyList(),
) : BaseUiState
