package com.jie.wealthmate.feature.menu.paymentMethodManagement

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.CategoryRepository

class PaymentMethodManagementScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<PaymentMethodManagementUiState>() {

    override val initialState: PaymentMethodManagementUiState
        get() = PaymentMethodManagementUiState()


    fun handleReorderCategoryItems(from: Int, to: Int) = reduceState { state ->
        val paymentMethodItems = state.paymentMethodItems.toMutableList()
        state.copy(
            paymentMethodItems = paymentMethodItems.apply { add(to, removeAt(from)) },
        )
    }
}