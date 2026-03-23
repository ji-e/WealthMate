package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.coroutines.launch

class EditPaymentMethodViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val paymentMethodId: String?,
) : BaseViewModel<EditPaymentMethodUiState>() {

    override val initialState: EditPaymentMethodUiState
        get() = EditPaymentMethodUiState(
            paymentMethodId = paymentMethodId
        )

    init {
        if (paymentMethodId.isNullOrBlank()) {
            getPaymentMethods()
        } else {
            getPaymentMethod()
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

    private fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods()
            .apiFlow { response ->
                reduceState { state ->
                    state.copy(
                        paymentMethodItems = response.map { it.mapperToVo() }
                    )
                }
            }
    }

    private fun getPaymentMethod() {
        paymentMethodId ?: return
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

    fun savePaymentMethod() {
        val uiState = container.uiState.value

        if (uiState.paymentMethodItems.any { it.groupId == uiState.group?.id && it.label == uiState.label.text }) {
            showSnackbar("이미 존재하는 결제수단입니다.")
            return
        }

        viewModelScope.launch {
            showLoading(true)
            try {
                if (paymentMethodId.isNullOrBlank()) {
                    paymentMethodRepository.insertPaymentMethod(
                        paymentMethodLabel = uiState.label.text,
                        paymentMethodGroupId = uiState.group?.id,
                        paymentMethodGroupLabel = uiState.group?.label,
                        sort = uiState.paymentMethodItems.size.toLong()
                    )
                } else {
                    paymentMethodRepository.updatePaymentMethod(
                        PaymentMethodEntity(
                            id = paymentMethodId,
                            label = uiState.label.text,
                            groupId = uiState.group?.id.default(),
                            groupLabel = uiState.group?.label.default(),
                            sort = uiState.sort
                        )
                    )
                }
                showSnackbar("결제수단이 저장되었습니다.")
                postSideEffect(EditPaymentMethodUiSideEffect.OnSuccess)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun removePaymentMethod() {
        paymentMethodId ?: return
        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.deletePaymentMethod(paymentMethodId)
                showSnackbar("결제수단이 삭제되었습니다.")
                postSideEffect(EditPaymentMethodUiSideEffect.OnSuccess)
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
