package com.jie.wealthmate.feature.menu.paymentMethodManagement.paymentMethodGroup

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
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.AddPaymentMethodScreenModel
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupList
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add
import wealthmate.composeapp.generated.resources.ic_delete

@Composable
fun PaymentMethodGroupModalBottomSheet(
    selectedPaymentMethodGroup: PaymentMethodGroupItemData? = null,
    onSelectClick: (PaymentMethodGroupItemData?) -> Unit,
    onRemoveClick: (onConfirmClick: () -> Unit) -> Unit,
    onSuccessRemove: () -> Unit = {},
    onDismissRequest: () -> Unit,
) {
    val screenModel: PaymentMethodGroupScreenModel = koinInject()
    val paymentMethodGroupItems = screenModel.paymentMethodGroupItems
    var tempSelectedPaymentMethodGroup by remember { mutableStateOf(selectedPaymentMethodGroup) }
    var addGroupLabel by remember { mutableStateOf(TextFieldValue("")) }

    var isModify by remember { mutableStateOf(false) }
    var isAdd by remember { mutableStateOf(false) }
    var isRemoved by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(paymentMethodGroupItems) {
        tempSelectedPaymentMethodGroup = when {
            isAdd -> paymentMethodGroupItems.find { it.label == addGroupLabel.text }
            isRemoved -> {
                if (selectedPaymentMethodGroup == tempSelectedPaymentMethodGroup) {
                    onSuccessRemove()
                }
                selectedPaymentMethodGroup
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
        trailingItem = {
            if (isModify.not()) {
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
                    .padding(horizontal = 20.dp)
                    .height(160.dp),
                listState = listState,
                paymentMethodGroupItems = paymentMethodGroupItems,
                tempSelectedPaymentMethodGroup = tempSelectedPaymentMethodGroup,
                onGroupClick = {
                    when {
                        isModify -> screenModel.showSnackbar("결제수단 그룹 수정을 완료해 주세요.")
                        isAdd -> screenModel.showSnackbar("결제수단 그룹 추가를 완료해 주세요.")
                        else -> tempSelectedPaymentMethodGroup = it
                    }
                }
            )

            if (isModify) {
                WMShadowDivider()

                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp),
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
                        supportingText = "15자 이내로 입력해 주세요.",
                        isCount = true,
                        modifier = Modifier.weight(1f)
                    )

                    WMIconButton(
                        iconRes = Res.drawable.ic_delete,
                        tint = ColorRed.Red_300,
                        onClick = {
                            isModify = false
                            onRemoveClick {
                                screenModel.removePaymentMethodGroup(tempSelectedPaymentMethodGroup)
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
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp),
                    value = addGroupLabel,
                    onValueChange = { addGroupLabel = it },
                    maxLength = 15,
                    label = "결제수단 그룹 추가",
                    placeholder = "포인트",
                    supportingText = "15자 이내로 입력해 주세요.",
                    isCount = true,
                )
            }
            // 버튼 영역 통합 및 최적화
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when {
                    isModify -> {
                        ActionButtons(
                            secondaryText = "취소",
                            onSecondaryClick = { isModify = false },
                            primaryText = "수정 완료",
                            onPrimaryClick = {
                                screenModel.updatePaymentMethodGroup(
                                    tempSelectedPaymentMethodGroup
                                )
                                isModify = false
                            }
                        )
                    }

                    isAdd -> {
                        ActionButtons(
                            secondaryText = "취소",
                            onSecondaryClick = { isAdd = false },
                            primaryText = "추가",
                            onPrimaryClick = { screenModel.addPaymentMethodGroup(addGroupLabel) }
                        )
                    }

                    else -> {
                        ActionButtons(
                            isSecondaryVisible = tempSelectedPaymentMethodGroup != null && tempSelectedPaymentMethodGroup?.id != AddPaymentMethodScreenModel.GROUP_ID_NONE,
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
        modifier = Modifier.weight(4f),
        onClick = onPrimaryClick
    )
}