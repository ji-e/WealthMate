package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.component.reorderable.ItemPosition
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.PaymentMethodVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.coroutines.launch

class PaymentMethodManagementViewModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseViewModel<PaymentMethodManagementUiState>() {

    var paymentMethodItems by mutableStateOf(
        listOf<PaymentMethodVo>()
    )

    override val initialState: PaymentMethodManagementUiState
        get() = PaymentMethodManagementUiState()

    init {
        getPaymentMethods()
    }

    fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods().apiFlow { response ->
            paymentMethodItems = response.map { it.paymentMethod.mapperToVo() }
        }
    }

    fun handleReorderPaymentMethodItems(from: ItemPosition, to: ItemPosition) {
        val currentItems = paymentMethodItems.toMutableList()
        val fromIndex = currentItems.indexOfFirst { it.id == from.key }
        val toIndex = currentItems.indexOfFirst { it.id == to.key }

        if (fromIndex != -1 && toIndex != -1) {
            currentItems.add(toIndex, currentItems.removeAt(fromIndex))
        }
        paymentMethodItems = currentItems
    }

    fun onDragOver(draggedOver: ItemPosition): Boolean {
        val currentItems = paymentMethodItems.toMutableList()
        return currentItems.any { it.id == draggedOver.key }
    }

    fun savePaymentMethodSort() {
        viewModelScope.launch {
            showLoading(true)
            try {
                paymentMethodRepository.updatePaymentMethodSort(
                    paymentMethodItems.mapIndexed { index, item ->
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
