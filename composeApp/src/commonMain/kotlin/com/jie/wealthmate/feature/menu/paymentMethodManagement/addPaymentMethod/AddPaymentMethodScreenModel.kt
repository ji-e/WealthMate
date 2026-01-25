package com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData
import com.jie.wealthmate.repository.CategoryRepository

class AddPaymentMethodScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<AddPaymentMethodUiState>() {
    private var paymentMethodItems: List<CategoryItemData> = emptyList()

    override val initialState: AddPaymentMethodUiState
        get() = AddPaymentMethodUiState()

    fun updateInit(
        paymentMethodItems: List<CategoryItemData>,
    ) {
        this.paymentMethodItems = paymentMethodItems
    }

    fun updatePaymentMethodLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                label = textFieldValue
            )
        }
    }


    fun savePaymentMethod() {
        val uiState = container.uiState.value

        if (paymentMethodItems.any { it.label == uiState.label.text }) {
            showSnackbar("존재하는 결제수단입니다.")
            return
        }

        launchSafe(
            block = {

            },
        ) {
            showSnackbar("결제수단이 저장되었습니다.")
            postSideEffect { AddPaymentMethodUiSideEffect.OnSuccessSave }
        }
    }
}
