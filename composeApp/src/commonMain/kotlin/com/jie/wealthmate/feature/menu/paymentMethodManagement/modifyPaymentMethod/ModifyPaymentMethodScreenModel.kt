package com.jie.wealthmate.feature.menu.paymentMethodManagement.modifyPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default

class ModifyPaymentMethodScreenModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<ModifyPaymentMethodUiState>() {
    private var paymentMethodId: String = ""

    override val initialState: ModifyPaymentMethodUiState
        get() = ModifyPaymentMethodUiState()

    fun updateInit(
        paymentMethodId: String,
    ) {
        this.paymentMethodId = paymentMethodId
        getPaymentMethod()
    }

    private fun getPaymentMethod() {
        launchSafe(
            block = {
                paymentMethodRepository.getPaymentMethodById(paymentMethodId)
            }
        ) { response ->
            response ?: return@launchSafe
            reduceState { state ->
                state.copy(
                    label = TextFieldValue(response.paymentMethod.label),
                    group = PaymentMethodGroupItemData(
                        id = response.group?.id.default(),
                        label = response.group?.label.default()
                    )
                )
            }
        }
    }
}
