package com.jie.wealthmate.feature.menu.paymentMethodManagement.modifyPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
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
                        label = response.group?.label.default(),
                    ),
                    sort = response.paymentMethod.sort
                )
            }
        }
    }

    fun updatePaymentMethodLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                label = textFieldValue
            )
        }
    }

    fun updatePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                group = paymentMethodGroup
            )
        }
    }

    fun savePaymentMethod() {
        launchSafe(
            block = {
                val uiState = container.uiState.value
                paymentMethodRepository.updatePaymentMethod(
                    PaymentMethodEntity(
                        id = paymentMethodId,
                        label = uiState.label.text,
                        groupId = uiState.group?.id.default(),
                        sort = uiState.sort
                    )
                )
            },
        ) {
            showSnackbar("결제수단이 수정되었습니다.")
            postSideEffect { ModifyPaymentMethodUiSideEffect.OnSuccess }
        }
    }
}
