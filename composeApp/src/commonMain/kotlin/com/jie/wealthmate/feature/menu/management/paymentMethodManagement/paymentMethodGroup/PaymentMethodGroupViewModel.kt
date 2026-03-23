package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod.EditPaymentMethodUiState
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentMethodGroupViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseViewModel<BaseUiState>() {
    var paymentMethodGroupItems by mutableStateOf(listOf<PaymentMethodGroupItemData>())
        private set

    override val initialState: BaseUiState
        get() = EditPaymentMethodUiState()

    init {
        getPaymentMethodGroups()
    }

    private fun getPaymentMethodGroups() {
        paymentMethodRepository.getPaymentMethodGroups()
            .apiFlow { response ->
                val groupItems = response.map {
                    PaymentMethodGroupItemData(
                        id = it.id,
                        label = it.label
                    )
                }.toMutableList().apply {
                    add(
                        PaymentMethodGroupItemData(
                            id = GROUP_ID_NONE,
                            label = "선택 안함"
                        )
                    )
                }

                println(groupItems)
                paymentMethodGroupItems = groupItems
            }
    }

    fun addPaymentMethodGroup(label: TextFieldValue) {
        val isExisted = paymentMethodGroupItems.any { it.label == label.text }
        if (isExisted) {
            viewModelScope.launch {
                delay(300)
                showSnackbar("이미 존재하는 결제수단 그룹 입니다.")
            }
            return
        }

        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.insertPaymentMethodGroup(label.text)
                getPaymentMethodGroups()
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun removePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.deletePaymentMethodGroup(paymentMethodGroup?.id.default())
                getPaymentMethodGroups()
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun updatePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        paymentMethodGroup ?: return

        val isExisted = paymentMethodGroupItems.any {
            it.id != paymentMethodGroup.id && it.label == paymentMethodGroup.label
        }
        if (isExisted) {
            viewModelScope.launch {
                delay(300)
                showSnackbar("이미 존재하는 결제수단 그룹 입니다.")
            }
            return
        }

        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.updatePaymentMethodGroup(
                    paymentMethodGroupId = paymentMethodGroup.id.default(),
                    paymentMethodGroupLabel = paymentMethodGroup.label
                )
                getPaymentMethodGroups()
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
