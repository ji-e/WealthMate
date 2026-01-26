@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.component.ShowPaymentMethodGroupModalBottomSheet
import com.jie.wealthmate.feature.menu.paymentMethodManagement.component.PaymentMethodItemData
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import org.koin.compose.koinInject

class AddPaymentMethodScreen(
    val paymentMethodItems: List<PaymentMethodItemData> = emptyList(),
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddPaymentMethodScreenModel = koinInject()
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

        val onBack: () -> Unit = remember(uiState.isDataChanged) {
            {
                showSaveBackDialog(
                    isShow = uiState.isDataChanged,
                    callback = { navigator.pop() }
                )
            }
        }

        BackHandler(
            enabled = true,
            onBack = onBack
        )

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
                paymentMethodItems = paymentMethodItems
            )
        }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is AddPaymentMethodUiSideEffect.OnSuccessSave -> navigator.pop()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
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
                onClick = screenModel::savePaymentMethod
            )
        }

        // 결제수단 그룹  ModalBottomSheet
        if (isShowPaymentMethodModalBottomSheet) {
            ShowPaymentMethodGroupModalBottomSheet(
                selectedPaymentMethodGroup = uiState.group,
                paymentMethodGroupItems = uiState.groupItems,
                onAddClick = screenModel::addPaymentMethodGroup,
                onSelectClick = screenModel::updatePaymentMethodGroup,
                onRemoveClick = { item ->
                    showRemoveDialog() {
                        screenModel.removePaymentMethodGroup(item)
                    }
                },
                onUpdateClick = screenModel::modifyPaymentMethodGroup,
                showSnackbar = screenModel::showSnackbar,
                onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
            )
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
