package com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.delay

class AddPaymentMethodScreenModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<AddPaymentMethodUiState>() {
    private var paymentMethodItems: List<CategoryItemData> = emptyList()

    override val initialState: AddPaymentMethodUiState
        get() = AddPaymentMethodUiState()

    fun updateInit(
        paymentMethodItems: List<CategoryItemData>,
    ) {
        this.paymentMethodItems = paymentMethodItems
        getPaymentMethodGroups()
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
                reduceState { state ->
                    state.copy(
                        group = groupItems.find { state.group?.id == it.id },
                        groupItems = groupItems
                    )
                }
            }
    }

    fun addPaymentMethodGroup(label: TextFieldValue) {
        launchSafe(
            block = {
                paymentMethodRepository.insertPaymentMethodGroup(label.text)
            }
        ) {
            getPaymentMethodGroups()
            delay(300)
            showSnackbar("결제수단 그룹이 추가되었습니다.")
        }
    }

    fun removePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        launchSafe(
            block = {
                paymentMethodRepository.deletePaymentMethodGroup(paymentMethodGroup?.id.default())
            }
        ) {
            getPaymentMethodGroups()
            delay(300)
            showSnackbar("결제수단 그룹이 삭제되었습니다.")
        }
    }

    fun modifyPaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        paymentMethodGroup ?: return
        launchSafe(
            block = {
                paymentMethodRepository.updatePaymentMethodGroup(
                    paymentMethodGroupId = paymentMethodGroup.id.default(),
                    label = paymentMethodGroup.label
                )
            }
        ) {
            getPaymentMethodGroups()
            delay(300)
            showSnackbar("결제수단 그룹이 수정되었습니다.")
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

    companion object {
        const val GROUP_ID_NONE = "none"
    }
}
