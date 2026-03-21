package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupModalBottomSheet
import com.jie.wealthmate.utils.default
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline

@Composable
fun ModifyPaymentMethodScreen(
    navController: NavController,
    paymentMethodId: String,
    viewModel: ModifyPaymentMethodViewModel = koinViewModel { parametersOf(paymentMethodId) },
) {
    BaseScreen(
        viewModel = viewModel,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is ModifyPaymentMethodUiSideEffect.OnSuccess -> navController.popBackStack()
            }
        }
    ) { uiState ->
        ModifyPaymentMethodContent(
            uiState = uiState,
            onBack = {
                if (uiState.isDataChanged) {
                    // TODO: show save back dialog
                    navController.popBackStack()
                } else {
                    navController.popBackStack()
                }
            },
            onRemovePaymentMethod = viewModel::removePaymentMethod,
            onUpdatePaymentMethodLabel = viewModel::updatePaymentMethodLabel,
            onUpdatePaymentMethodGroup = viewModel::updatePaymentMethodGroup,
            onModifyPaymentMethod = viewModel::modifyPaymentMethod
        )
    }
}

@Composable
fun ModifyPaymentMethodContent(
    uiState: ModifyPaymentMethodUiState,
    onBack: () -> Unit,
    onRemovePaymentMethod: () -> Unit,
    onUpdatePaymentMethodLabel: (TextFieldValue) -> Unit,
    onUpdatePaymentMethodGroup: (PaymentMethodGroupItemData?) -> Unit,
    onModifyPaymentMethod: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }
    var isShowRemoveDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        WMTopBar(
            title = TopBarItem.Title("${MenuEnum.PAYMENT_METHOD.label} 수정"),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_delete_outline,
                    action = { isShowRemoveDialog = true }
                )
            )
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
            text = "수정",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            enabled = uiState.label.text.isNotBlank(),
            onClick = onModifyPaymentMethod
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
    
    // TODO: handle isShowRemoveDialog
}
