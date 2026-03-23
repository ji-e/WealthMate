package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.SpacerSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMRemoveDialog
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.WMSpacer
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.editPaymentMethod.component.PaymentMethodGroupItemData
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupModalBottomSheet
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline

@Composable
fun EditPaymentMethodScreen(
    navController: NavController,
    paymentMethodId: String?,
    viewModel: EditPaymentMethodViewModel = koinViewModel {
        parametersOf(paymentMethodId)
    },
) {
    var isShowSaveBackDialog by remember { mutableStateOf(false) }
    var isShowRemoveDialog by remember { mutableStateOf(false) }

    val onBack: () -> Unit = {
        if (viewModel.container.uiState.value.isDataChanged) {
            isShowSaveBackDialog = true
        } else {
            navController.popBackStack()
        }
    }

    BaseScreen(
        viewModel = viewModel,
        onBack = onBack,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is EditPaymentMethodUiSideEffect.OnSuccess -> {
                    navController.popBackStack()
                }
            }
        }
    ) { uiState ->
        EditPaymentMethodContent(
            uiState = uiState,
            onBack = onBack,
            onRemove = { isShowRemoveDialog = true },
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
        if (isShowRemoveDialog) {
            WMRemoveDialog(
                onConfirm = {
                    isShowRemoveDialog = false
                    viewModel.removePaymentMethod()
                },
                onDismiss = { isShowRemoveDialog = false }
            )
        }
    }
}

@Composable
fun EditPaymentMethodContent(
    uiState: EditPaymentMethodUiState,
    onBack: () -> Unit,
    onRemove: () -> Unit,
    onUpdatePaymentMethodLabel: (TextFieldValue) -> Unit,
    onUpdatePaymentMethodGroup: (PaymentMethodGroupItemData?) -> Unit,
    onSavePaymentMethod: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowPaymentMethodModalBottomSheet by remember { mutableStateOf(false) }
    var isShowGroupRemoveDialog by remember { mutableStateOf(false) }
    var onConfirmGroupRemove by remember { mutableStateOf<(() -> Unit)?>(null) }

    val isEditMode = uiState.paymentMethodId.isNullOrBlank().not()

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        WMTopBar(
            title = TopBarItem.Title(
                "${MenuEnum.PAYMENT_METHOD.label} ${if (isEditMode) "수정" else "추가"}"
            ),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = if (isEditMode) {
                listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_delete_outline,
                        action = onRemove
                    )
                )
            } else {
                null
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Padding.BackgroundHorizontal)
                .verticalScroll(rememberScrollState())
        ) {
            WMTextField(
                value = uiState.label,
                onValueChange = onUpdatePaymentMethodLabel,
                modifier = Modifier.padding(top = Padding.SpacerXXS),
                label = "결제수단 이름",
                placeholder = "삼성카드",
                isRequire = true,
                maxLength = 15,
                isCount = true,
            )

            WMSpacer(size = SpacerSize.LARGE)
        }

        WMButton(
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = Padding.BackgroundHorizontal)
                .fillMaxWidth(),
            enabled = uiState.label.text.isNotBlank(),
            onClick = onSavePaymentMethod
        )
        WMSpacer(size = SpacerSize.LARGE)
    }

    if (isShowPaymentMethodModalBottomSheet) {
        PaymentMethodGroupModalBottomSheet(
            selectedPaymentMethodGroup = uiState.group,
            onSelectClick = {
                onUpdatePaymentMethodGroup(it)
                isShowPaymentMethodModalBottomSheet = false
            },
            onRemoveClick = { onConfirmClick ->
                onConfirmGroupRemove = onConfirmClick
                isShowGroupRemoveDialog = true
            },
            onGroupLabelChange = onUpdatePaymentMethodGroup,
            onSuccessRemove = { onUpdatePaymentMethodGroup(null) },
            onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
        )
    }

    if (isShowGroupRemoveDialog) {
        WMRemoveDialog(
            onConfirm = {
                onConfirmGroupRemove?.invoke()
                isShowGroupRemoveDialog = false
            },
            onDismiss = { isShowGroupRemoveDialog = false }
        )
    }
}

@Preview
@Composable
private fun EditPaymentMethodContentPreview() {
    WMTheme {
        EditPaymentMethodContent(
            uiState = EditPaymentMethodUiState(),
            onBack = {},
            onRemove = {},
            onUpdatePaymentMethodLabel = {},
            onUpdatePaymentMethodGroup = {},
            onSavePaymentMethod = {}
        )
    }
}
