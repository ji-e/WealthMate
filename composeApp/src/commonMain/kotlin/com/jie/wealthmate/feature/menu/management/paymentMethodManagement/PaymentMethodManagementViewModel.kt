package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.coroutines.launch

class PaymentMethodManagementViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseViewModel<PaymentMethodManagementUiState>() {

    override val initialState: PaymentMethodManagementUiState
        get() = PaymentMethodManagementUiState()

    init {
        getPaymentMethods()
    }

    fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods().apiFlow { response ->
            reduceState { state ->
                state.copy(
                    paymentMethodItems = response.map { it.paymentMethod.mapperToVo() }
                )
            }
        }
    }

    fun handleReorderPaymentMethodItems(from: Int, to: Int) = reduceState { state ->
        val currentItems = state.paymentMethodItems.toMutableList()
        if (from !in currentItems.indices || to !in currentItems.indices) return@reduceState state
        
        currentItems.add(to, currentItems.removeAt(from))
        state.copy(paymentMethodItems = currentItems)
    }

    fun savePaymentMethodSort() {
        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.updatePaymentMethodSort(
                    container.uiState.value.paymentMethodItems.mapIndexed { index, item -> 
                        item.id.default() to index.toLong() 
                    }
                )
                showSnackbar("저장되었습니다.")
            } catch (e: Exception) {
                showSnackbar(e.message ?: "결제 수단 저장에 실패했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }
}
