package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.component.PaymentMethodItemData
import com.jie.wealthmate.repository.PaymentMethodRepository

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
                        paymentMethodItems = response.map {
                            PaymentMethodItemData(
                                id = it.paymentMethod.id,
                                label = it.paymentMethod.label,
                                groupId = it.group?.id,
                                groupLabel = it.group?.label,
                                sort = it.paymentMethod.sort
                            )
                        }
                    )
                }
            }
    }


    fun handleReorderCategoryItems(from: Int, to: Int) = reduceState { state ->
        val paymentMethodItems = state.paymentMethodItems.toMutableList()
        state.copy(
            paymentMethodItems = paymentMethodItems.apply { add(to, removeAt(from)) },
        )
    }

    fun savePaymentMethodSort() {
        launchSafe(
            block = {
                val paymentMethodItems = container.uiState.value.paymentMethodItems
                paymentMethodRepository.updatePaymentMethodSort(
                    paymentMethodItems.mapIndexed { index, item -> item.id to index.toLong() }
                )
            },
        ) {
            showSnackbar("저장되었습니다.")
        }
    }


}