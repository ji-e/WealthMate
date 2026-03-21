package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodWithGroupEntity
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.launch

class ModifyPaymentMethodViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseViewModel<ModifyPaymentMethodUiState>() {
    private var paymentMethodItems: List<PaymentMethodWithGroupEntity> = emptyList()
    private var paymentMethodId: String = ""

    override val initialState: ModifyPaymentMethodUiState
        get() = ModifyPaymentMethodUiState()

    fun updateInit(
        paymentMethodId: String,
    ) {
        this.paymentMethodId = paymentMethodId
        getPaymentMethods()
        getPaymentMethod()
    }

    private fun getPaymentMethod() {
        viewModelScope.launch {
            showLoading(true)
            try {
                val response = paymentMethodRepository.getPaymentMethodById(paymentMethodId)
                if (response != null) {
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
            } catch (e: Exception) {
                showSnackbar(e.message ?: "결제 수단을 불러오지 못했습니다.")
            } finally {
                showLoading(false)
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

    fun modifyPaymentMethod() {
        val uiState = container.uiState.value
        val isExisted = paymentMethodItems.any {
            it.paymentMethod.id != paymentMethodId && it.group?.id == uiState.group?.id && it.paymentMethod.label == uiState.label.text
        }
        if (isExisted) {
            showSnackbar("이미 존재하는 결제수단 입니다.")
            return
        }

        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.updatePaymentMethod(
                    PaymentMethodEntity(
                        id = paymentMethodId,
                        label = uiState.label.text,
                        groupId = uiState.group?.id.default(),
                        groupLabel = uiState.group?.label.default(),
                        sort = uiState.sort
                    )
                )
                showSnackbar("결제수단이 수정되었습니다.")
                postSideEffect(ModifyPaymentMethodUiSideEffect.OnSuccess)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun removePaymentMethod() {
        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.deletePaymentMethod(paymentMethodId)
                showSnackbar("결제수단이 삭제되었습니다.")
                postSideEffect(ModifyPaymentMethodUiSideEffect.OnSuccess)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods()
            .apiFlow(showLoadingIndicator = false) { response ->
                paymentMethodItems = response
            }
    }
}
