package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo

class PaymentMethodManagementScreenModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<PaymentMethodManagementUiState>() {

    override val initialState: PaymentMethodManagementUiState
        get() = PaymentMethodManagementUiState()

    init {
        getPaymentMethods()
    }

    fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods()
            .apiFlow { response ->
                println("response: $response")
                reduceState { state ->
                    state.copy(
                        paymentMethodItems = response.map { it.paymentMethod.mapperToVo() }
                    )
                }
            }
    }


    fun handleReorderCategoryItems(from: Int, to: Int) = reduceState { state ->
        val paymentMethodItems = state.paymentMethodItems.default().toMutableList()
        state.copy(
            paymentMethodItems = paymentMethodItems.apply { add(to, removeAt(from)) },
        )
    }

    fun savePaymentMethodSort() {
        launchSafe(
            block = {
                val paymentMethodItems = container.uiState.value.paymentMethodItems
                paymentMethodRepository.updatePaymentMethodSort(
                    paymentMethodItems.default()
                        .mapIndexed { index, item -> item.id to index.toLong() }
                )
            },
        ) {
            showSnackbar("저장되었습니다.")
        }
    }
}