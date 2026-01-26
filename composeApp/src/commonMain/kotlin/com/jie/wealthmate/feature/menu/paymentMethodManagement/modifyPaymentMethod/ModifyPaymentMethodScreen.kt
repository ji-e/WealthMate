@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.paymentMethodManagement.modifyPaymentMethod

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
import com.jie.wealthmate.feature.menu.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupModalBottomSheet
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete

class ModifyPaymentMethodScreen(
    val paymentMethodId: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: ModifyPaymentMethodScreenModel = koinInject()
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

        if (navigator.lastItem is ModifyPaymentMethodScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("${MenuEnum.PAYMENT_METHOD.label} 수정"),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { onBack() }
                    ),
                    trailingItem = listOf(
                        TopBarItem.TrailingItem(
                            iconRes = Res.drawable.ic_delete,
                            tint = ColorRed.Red_300,
                            action = {
                                showRemoveDialog() {
                                    screenModel.removePaymentMethod()
                                }
                            }
                        )
                    )
                )
            }
        }

        LaunchedEffect(Unit) {
            screenModel.updateInit(
                paymentMethodId = paymentMethodId
            )
        }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is ModifyPaymentMethodUiSideEffect.OnSuccess -> navigator.pop()
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
            PaymentMethodGroupModalBottomSheet(
                selectedPaymentMethodGroup = uiState.group,
                onSelectClick = screenModel::updatePaymentMethodGroup,
                onRemoveClick = { onConfirmClick ->
                    showRemoveDialog() {
                        onConfirmClick()
                    }
                },
                onDismissRequest = { isShowPaymentMethodModalBottomSheet = false }
            )
        }
    }
}
