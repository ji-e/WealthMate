package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.AddPaymentMethodUiState
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.utils.default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentMethodGroupScreenModel(
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<BaseUiState>() {
    var paymentMethodGroupItems by mutableStateOf(listOf<PaymentMethodGroupItemData>())
        private set

    override val initialState: BaseUiState
        get() = AddPaymentMethodUiState()

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
            screenScope.launch {
                delay(300)
                showSnackbar("이미 존재하는 결제수단 그룹 입니다.")
            }
            return
        }

        launchSafe(
            block = {
                paymentMethodRepository.insertPaymentMethodGroup(label.text)
            }
        ) {
            getPaymentMethodGroups()
        }
    }

    fun removePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        launchSafe(
            block = {
                paymentMethodRepository.deletePaymentMethodGroup(paymentMethodGroup?.id.default())
            }
        ) {
            getPaymentMethodGroups()
        }
    }

    fun updatePaymentMethodGroup(paymentMethodGroup: PaymentMethodGroupItemData?) {
        paymentMethodGroup ?: return

        val isExisted = paymentMethodGroupItems.any {
            it.id != paymentMethodGroup.id && it.label == paymentMethodGroup.label
        }
        if (isExisted) {
            screenScope.launch {
                delay(300)
                showSnackbar("이미 존재하는 결제수단 그룹 입니다.")
            }
            return
        }

        launchSafe(
            block = {
                paymentMethodRepository.updatePaymentMethodGroup(
                    paymentMethodGroupId = paymentMethodGroup.id.default(),
                    paymentMethodGroupLabel = paymentMethodGroup.label
                )
            }
        ) {
            getPaymentMethodGroups()
        }
    }

    companion object {
        const val GROUP_ID_NONE = "none"
    }
}
