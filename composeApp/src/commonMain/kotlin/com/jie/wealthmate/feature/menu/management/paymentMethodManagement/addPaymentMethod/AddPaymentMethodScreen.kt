package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupModalBottomSheet
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.PaymentMethodVo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddPaymentMethodScreen(
    navController: NavController,
    paymentMethodItems: List<PaymentMethodVo> = emptyList(),
    viewModel: AddPaymentMethodViewModel = koinViewModel { parametersOf(paymentMethodItems) },
) {
    val uiState by viewModel.container.uiState.collectAsState()
    var isShowSaveBackDialog by remember { mutableStateOf(false) }

    BaseScreen(
        viewModel = viewModel,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is AddPaymentMethodUiSideEffect.OnSuccessSave -> navController.popBackStack()
            }
        }
    ) {
        AddPaymentMethodContent(
            uiState = uiState,
            onBack = {
                if (uiState.isDataChanged) {
                    isShowSaveBackDialog = true
                } else {
                    navController.popBackStack()
                }
            },
            onUpdatePaymentMethodLabel = viewModel::updatePaymentMethodLabel,
            onUpdatePaymentMethodGroup = viewModel::updatePaymentMethodGroup,
            onSavePaymentMethod = viewModel::savePaymentMethod
        )

        if (isShowSaveBackDialog) {
            WMSaveBackDialog(
                onConfirm = {
                    isShowSaveBackDialog = false
                    navController.popBackStack()
                },
                onDismiss = { isShowSaveBackDialog = false }
            )
        }

    }
}

@Composable
fun AddPaymentMethodContent(
    uiState: AddPaymentMethodUiState,
    onBack: () -> Unit,
    onUpdatePaymentMethodLabel: (TextFieldValue) -> Unit,
    onUpdatePaymentMethodGroup: (PaymentMethodGroupItemData?) -> Unit,
    onSavePaymentMethod: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        WMTopBar(
            title = TopBarItem.Title("${MenuEnum.PAYMENT_METHOD.label} 추가"),
            readingItem = TopBarItem.ReadingItem(action = onBack),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 28.dp)
        ) {
            WMTextField(
                value = uiState.label,
                onValueChange = onUpdatePaymentMethodLabel,
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
                modifier = Modifier.padding(top = 4.dp),
                label = "결제수단 그룹",
                placeholder = "신용카드",
                readOnly = true,
                onReadOnlyClick = { isShowPaymentMethodModalBottomSheet = true }
            )
        }

        WMButton(
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            enabled = uiState.label.text.isNotBlank(),
            onClick = onSavePaymentMethod
        )
    }

    if (isShowPaymentMethodModalBottomSheet) {
        PaymentMethodGroupModalBottomSheet(
            selectedPaymentMethodGroup = uiState.group,
            onSelectClick = {
                onUpdatePaymentMethodGroup(it)
                isShowPaymentMethodModalBottomSheet = false
            },
            onRemoveClick = { onConfirmClick ->
                // TODO: show remove dialog
                onConfirmClick()
            },
            onGroupLabelChange = onUpdatePaymentMethodGroup,
            onSuccessRemove = { onUpdatePaymentMethodGroup(null) },
            onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
        )
    }
}
