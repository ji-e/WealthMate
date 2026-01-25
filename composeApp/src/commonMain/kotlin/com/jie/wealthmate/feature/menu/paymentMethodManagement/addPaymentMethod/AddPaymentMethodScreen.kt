@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.ButtonStyle
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMModalBottomSheet
import com.jie.wealthmate.component.WMShadowDivider
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categoryManagement.component.CategoryItemData
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupList
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add
import wealthmate.composeapp.generated.resources.ic_delete

class AddPaymentMethodScreen(
    val paymentMethodItems: List<CategoryItemData> = emptyList(),
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddPaymentMethodScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

        fun onBack() {
            showSaveBackDialog(uiState.label.text.isNotEmpty()) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        if (navigator.lastItem is AddPaymentMethodScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("${MenuEnum.PAYMENT_METHOD.label} 추가"),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { onBack() }
                    ),
                )
            }
        }

        LaunchedEffect(Unit) {
            screenModel.updateInit(
                paymentMethodItems = paymentMethodItems,
            )
        }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is AddPaymentMethodUiSideEffect.OnSuccessSave -> {
                    navigator.pop()
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.weight(1f).padding(horizontal = 20.dp)) {
                // 현금, 체크카드, 신용카드, 선불식, 계좌,
                WMTextField(
                    value = uiState.label,
                    onValueChange = screenModel::updatePaymentMethodLabel,
                    modifier = Modifier.padding(top = 4.dp),
                    label = "결제수단 이름",
                    placeholder = "삼성카드",
                    isRequire = true,
                    maxLength = 15,
                    isCount = true,
                )

                WMTextField(
                    value = uiState.group?.label.default(),
                    onValueChange = { },
                    modifier = Modifier.padding(top = 16.dp),
                    label = "결제수단 그룹",
                    placeholder = "신용카드",
                    readOnly = true,
                    onReadOnlyClick = { isShowPaymentMethodModalBottomSheet = true }
                )
            }
            // 저장 버튼
            WMButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.label.text.isNotBlank(),
                onClick = { screenModel.savePaymentMethod() }
            )
        }
        if (isShowPaymentMethodModalBottomSheet) {
            ShowPaymentMethodGroupModalBottomSheet(
                selectedPaymentMethodGroup = uiState.group,
                paymentMethodGroupItems = uiState.groupItems,
                onAddClick = screenModel::addPaymentMethodGroup,
                onSelectClick = screenModel::updatePaymentMethodGroup,
                onRemoveClick = {
                    showRemoveDialog() {
                        screenModel.removePaymentMethodGroup(it)
                    }
                },
                onUpdateClick = screenModel::modifyPaymentMethodGroup,
                onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
            )
        }
    }

    @Composable
    private fun ShowPaymentMethodGroupModalBottomSheet(
        selectedPaymentMethodGroup: PaymentMethodGroupItemData? = null,
        paymentMethodGroupItems: List<PaymentMethodGroupItemData>,
        onAddClick: (TextFieldValue) -> Unit = {},
        onSelectClick: (PaymentMethodGroupItemData) -> Unit = {},
        onRemoveClick: (PaymentMethodGroupItemData?) -> Unit = {},
        onUpdateClick: (PaymentMethodGroupItemData?) -> Unit = {},
        onDismissRequest: () -> Unit,
    ) {
        var isModify by remember { mutableStateOf(false) }
        var isAdd by remember { mutableStateOf(false) }
        var tempSelectedPaymentMethodGroup by remember {
            mutableStateOf<PaymentMethodGroupItemData?>(selectedPaymentMethodGroup)
        }
        var addGroupLabel by remember { mutableStateOf(TextFieldValue("")) }

        val listState = rememberLazyListState()
        LaunchedEffect(paymentMethodGroupItems) {
            tempSelectedPaymentMethodGroup =
                if (isAdd) paymentMethodGroupItems.find { it.label == addGroupLabel.text }
                else if (isModify) tempSelectedPaymentMethodGroup
                else selectedPaymentMethodGroup

            val movePosition = paymentMethodGroupItems.indexOf(tempSelectedPaymentMethodGroup)
                .run { if (this <= 0) 0 else this - 1 }

            listState.scrollToItem(movePosition)

            addGroupLabel = TextFieldValue("")
            isModify = false
            isAdd = false
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
                modifier = Modifier
                    .padding(bottom = 20.dp)
            ) {
                PaymentMethodGroupList(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .height(160.dp),
                    listState = listState,
                    paymentMethodGroupItems = paymentMethodGroupItems,
                    tempSelectedPaymentMethodGroup = tempSelectedPaymentMethodGroup,
                    onGroupClick = {
                        if (isModify.not() && isAdd.not()) {
                            tempSelectedPaymentMethodGroup = it
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
                                onRemoveClick(tempSelectedPaymentMethodGroup)
                                tempSelectedPaymentMethodGroup = null
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

                if (isModify) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        WMButton(
                            text = "취소",
                            buttonStyle = ButtonStyle.TONAL,
                            buttonSize = ButtonSize.LARGE,
                            modifier = Modifier.weight(1f),
                            onClick = { isModify = false },
                        )

                        WMButton(
                            text = "수정 완료",
                            buttonStyle = ButtonStyle.FILLED,
                            buttonSize = ButtonSize.LARGE,
                            modifier = Modifier.weight(4f),
                            onClick = { onUpdateClick(tempSelectedPaymentMethodGroup) }
                        )
                    }
                } else if (isAdd) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        WMButton(
                            text = "취소",
                            buttonStyle = ButtonStyle.TONAL,
                            buttonSize = ButtonSize.LARGE,
                            modifier = Modifier.weight(1f),
                            onClick = { isAdd = false },
                        )

                        WMButton(
                            text = "추가",
                            buttonStyle = ButtonStyle.FILLED,
                            buttonSize = ButtonSize.LARGE,
                            modifier = Modifier.weight(4f),
                            onClick = { onAddClick(addGroupLabel) }
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (tempSelectedPaymentMethodGroup?.id != "notting") {
                            WMButton(
                                text = "수정",
                                buttonStyle = ButtonStyle.TONAL,
                                buttonSize = ButtonSize.LARGE,
                                modifier = Modifier.weight(1f),
                                onClick = { isModify = true },
                            )
                        }

                        WMButton(
                            text = "선택",
                            buttonStyle = ButtonStyle.FILLED,
                            buttonSize = ButtonSize.LARGE,
                            modifier = Modifier.weight(4f),
                            onClick = {
                                onDismissRequest()
                                tempSelectedPaymentMethodGroup?.let {
                                    onSelectClick(it)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun AddPaymentMethodScreenPreview() {
        WMTheme {
            AddPaymentMethodScreen()
        }
    }
}
