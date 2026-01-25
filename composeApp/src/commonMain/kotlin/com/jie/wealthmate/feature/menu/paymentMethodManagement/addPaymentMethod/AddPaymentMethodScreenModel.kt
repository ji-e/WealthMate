package com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.CategoryRepository

class AddPaymentMethodScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<AddPaymentMethodUiState>() {
    private var paymentMethodItems: List<CategoryItemData> = emptyList()

    override val initialState: AddPaymentMethodUiState
        get() = AddPaymentMethodUiState(
            groupItems = listOf(
                PaymentMethodGroupItemData(id = "1", label = "신용카드"),
                PaymentMethodGroupItemData(id = "2", label = "체크카드"),
                PaymentMethodGroupItemData(id = "3", label = "현금"),
                PaymentMethodGroupItemData(id = "notting", label = "선택 안함")
            )
        )

    fun updateInit(
        paymentMethodItems: List<CategoryItemData>,
    ) {
        this.paymentMethodItems = paymentMethodItems
    }

    fun updatePaymentMethodLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                label = textFieldValue
            )
        }
    }

    fun updatePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData) {
        reduceState { state ->
            state.copy(
                group = paymentMethodGroup
            )
        }
    }

    fun addPaymentMethodGroup(label: TextFieldValue) {
        // todo db
        reduceState { state ->
            state.copy(
                groupItems = state.groupItems.toMutableList()
                    .apply {
                        add(
                            index = this.lastIndex,
                            element = PaymentMethodGroupItemData(
                                id = label.text,
                                label = label.text
                            )
                        )
                    }
            )
        }
    }

    fun removePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        // todo db
        reduceState { state ->
            state.copy(
                groupItems = state.groupItems.toMutableList()
                    .apply { remove(paymentMethodGroup) },
                group = if (state.group == paymentMethodGroup) null else state.group,
            )
        }
    }

    fun modifyPaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        paymentMethodGroup ?: return
        // todo db
        reduceState { state ->
            state.copy(
                groupItems = state.groupItems.toMutableList()
                    .apply {
                        val index = this.indexOf(this.find { it.id == paymentMethodGroup.id })
                        this[index] = paymentMethodGroup
                    },
                group = paymentMethodGroup
            )
        }
    }

    fun savePaymentMethod() {
        val uiState = container.uiState.value

        if (paymentMethodItems.any { it.label == uiState.label.text }) {
            showSnackbar("존재하는 결제수단입니다.")
            return
        }

        launchSafe(
            block = {

            },
        ) {
            showSnackbar("결제수단이 저장되었습니다.")
            postSideEffect { AddPaymentMethodUiSideEffect.OnSuccessSave }
        }
    }
}
