package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMShadowDivider
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.AddPaymentMethodViewModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupList
import com.jie.wealthmate.utils.default
import org.koin.compose.viewmodel.koinViewModel
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add
import wealthmate.composeapp.generated.resources.ic_delete_outline

@Composable
fun PaymentMethodGroupModalBottomSheet(
    selectedPaymentMethodGroup: PaymentMethodGroupItemData? = null,
    onSelectClick: (PaymentMethodGroupItemData?) -> Unit,
    onRemoveClick: (onConfirmClick: () -> Unit) -> Unit,
    onGroupLabelChange: (PaymentMethodGroupItemData?) -> Unit,
    onSuccessRemove: () -> Unit = {},
    onDismissRequest: () -> Unit,
) {
    val viewModel: PaymentMethodGroupViewModel = koinViewModel()
    val paymentMethodGroupItems = viewModel.paymentMethodGroupItems
    var tempSelectedPaymentMethodGroup by remember { mutableStateOf(selectedPaymentMethodGroup) }
    var addGroupLabel by remember { mutableStateOf(TextFieldValue("")) }

    var isModify by remember { mutableStateOf(false) }
    var isAdd by remember { mutableStateOf(false) }
    var isRemoved by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(paymentMethodGroupItems) {
        tempSelectedPaymentMethodGroup =
            when {
                isAdd -> {
                    paymentMethodGroupItems.find { it.label == addGroupLabel.text }
                }

                isRemoved -> {
                    if (selectedPaymentMethodGroup == tempSelectedPaymentMethodGroup) {
                        onSuccessRemove()
                        null
                    } else {
                        selectedPaymentMethodGroup
                    }
                }

                else -> tempSelectedPaymentMethodGroup
            }

        val index = paymentMethodGroupItems.indexOf(tempSelectedPaymentMethodGroup)
        val movePosition = if (index <= 0) 0 else index - 1
        listState.scrollToItem(movePosition)

        addGroupLabel = TextFieldValue("")
        isModify = false
        isAdd = false
        isRemoved = false
    }

    WMModalBottomSheet(
        title = "결제수단 그룹",
        readingItem = {
            if (isModify.not() && paymentMethodGroupItems.size < 10) {
                WMIconButton(
                    iconRes = Res.drawable.ic_add,
                    onClick = { isAdd = true },
                )
            }
        },
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            PaymentMethodGroupList(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 20.dp)
                    .height(200.dp),
                listState = listState,
                paymentMethodGroupItems = paymentMethodGroupItems,
                tempSelectedPaymentMethodGroup = tempSelectedPaymentMethodGroup,
                onGroupClick = {
                    when {
                        isModify -> viewModel.showSnackbar("결제수단 그룹 수정을 완료해 주세요.")
                        isAdd -> viewModel.showSnackbar("결제수단 그룹 추가를 완료해 주세요.")
                        else -> tempSelectedPaymentMethodGroup = it
                    }
                }
            )

            if (isModify) {
                WMShadowDivider()

                Row(
                    modifier = Modifier
                        .padding(start = 28.dp, end = 12.dp)
                        .padding(top = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WMTextField(
                        value = tempSelectedPaymentMethodGroup?.label.default(),
                        onValueChange = {
                            tempSelectedPaymentMethodGroup =
                                tempSelectedPaymentMethodGroup?.copy(label = it)
                        },
                        maxLength = 15,
                        label = "결제수단 그룹 이름 수정",
                        placeholder = tempSelectedPaymentMethodGroup?.label.default(),
                        isCount = true,
                        modifier = Modifier.weight(1f)
                    )

                    WMIconButton(
                        iconRes = Res.drawable.ic_delete_outline,
                        onClick = {
                            onRemoveClick {
                                viewModel.removePaymentMethodGroup(tempSelectedPaymentMethodGroup)
                                isRemoved = true
                            }
                        }
                    )
                }
            }

            if (isAdd) {
                WMShadowDivider()

                WMTextField(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 32.dp),
                    value = addGroupLabel,
                    onValueChange = { addGroupLabel = it },
                    maxLength = 15,
                    label = "결제수단 그룹 추가",
                    placeholder = "포인트",
                    isCount = true,
                )
            }
            // 버튼 영역 통합 및 최적화
            Row(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when {
                    isModify -> {
                        ActionButtons(
                            secondaryText = "취소",
                            onSecondaryClick = { isModify = false },
                            primaryText = "수정 완료",
                            onPrimaryClick = {
                                viewModel.updatePaymentMethodGroup(
                                    tempSelectedPaymentMethodGroup
                                )
                                if (tempSelectedPaymentMethodGroup?.id == selectedPaymentMethodGroup?.id) {
                                    onGroupLabelChange(tempSelectedPaymentMethodGroup)
                                }
                                isModify = false
                            }
                        )
                    }

                    isAdd -> {
                        ActionButtons(
                            secondaryText = "취소",
                            onSecondaryClick = { isAdd = false },
                            primaryText = "추가",
                            onPrimaryClick = { viewModel.addPaymentMethodGroup(addGroupLabel) }
                        )
                    }

                    else -> {
                        val isSecondaryVisible =
                            tempSelectedPaymentMethodGroup?.id != AddPaymentMethodViewModel.GROUP_ID_NONE &&
                                    tempSelectedPaymentMethodGroup?.id.isNullOrEmpty().not()

                        ActionButtons(
                            isSecondaryVisible = isSecondaryVisible,
                            secondaryText = "수정",
                            onSecondaryClick = { isModify = true },
                            primaryText = "선택",
                            onPrimaryClick = {
                                onDismissRequest()
                                tempSelectedPaymentMethodGroup?.let(onSelectClick)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.ActionButtons(
    isSecondaryVisible: Boolean = true,
    secondaryText: String,
    onSecondaryClick: () -> Unit,
    primaryText: String,
    onPrimaryClick: () -> Unit,
) {
    if (isSecondaryVisible) {
        WMButton(
            text = secondaryText,
            buttonStyle = ButtonStyle.TONAL,
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier.weight(1f),
            onClick = onSecondaryClick,
        )
    }
    WMButton(
        text = primaryText,
        buttonStyle = ButtonStyle.FILLED,
        buttonSize = ButtonSize.LARGE,
        modifier = Modifier.weight(3f),
        onClick = onPrimaryClick
    )
}
