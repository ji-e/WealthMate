package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.coroutines.launch

class AddPaymentMethodViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseViewModel<AddPaymentMethodUiState>() {
    private var paymentMethodItems: List<PaymentMethodVo> = emptyList()

    override val initialState: AddPaymentMethodUiState
        get() = AddPaymentMethodUiState()

    fun updateInit(
        paymentMethodItems: List<PaymentMethodVo>,
    ) {
        this.paymentMethodItems = paymentMethodItems
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
        val uiState = container.uiState.value

        if (paymentMethodItems.any { it.groupId == uiState.group?.id && it.label == uiState.label.text }) {
            showSnackbar("존재하는 결제수단입니다.")
            return
        }

        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.insertPaymentMethod(
                    paymentMethodLabel = uiState.label.text,
                    paymentMethodGroupId = uiState.group?.id,
                    paymentMethodGroupLabel = uiState.group?.label,
                    sort = paymentMethodItems.size.toLong()
                )
                showSnackbar("결제수단이 저장되었습니다.")
                postSideEffect(AddPaymentMethodUiSideEffect.OnSuccessSave)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    companion object Companion {
        const val GROUP_ID_NONE = "none"
    }
}
