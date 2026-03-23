package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jie.wealthmate.base.BackHandlerWrapper
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMSaveBackDialog
import com.jie.wealthmate.component.reorderable.ItemPosition
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.component.PaymentMethod
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.PaymentMethodVo
import org.koin.compose.viewmodel.koinViewModel
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

@Composable
fun PaymentMethodManagementScreen(
    navController: NavController,
    viewModel: PaymentMethodManagementViewModel = koinViewModel(),
) {
    val uiState by viewModel.container.uiState.collectAsState()

    BaseScreen(viewModel = viewModel) {
        PaymentMethodManagementContent(
            paymentMethodItems = viewModel.paymentMethodItems,
            onBack = { navController.popBackStack() },
            onAddItem = { navController.navigate("addPaymentMethod") },
            onItemClick = { navController.navigate("modifyPaymentMethod/${it.id}") },
            onSaveSort = viewModel::savePaymentMethodSort,
            onReorderCancel = viewModel::getPaymentMethods,
            onMove = viewModel::handleReorderPaymentMethodItems,
            onDragOver = viewModel::onDragOver
        )
    }
}

@Composable
fun PaymentMethodManagementContent(
    paymentMethodItems: List<PaymentMethodVo>,
    onBack: () -> Unit,
    onAddItem: () -> Unit,
    onItemClick: (PaymentMethodVo) -> Unit,
    onSaveSort: () -> Unit,
    onReorderCancel: () -> Unit,
    onMove: (ItemPosition, ItemPosition) -> Unit,
    onDragOver: (ItemPosition) -> Boolean,
    modifier: Modifier = Modifier,
) {
    var isShowSaveBackDialog by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }

    val listState = rememberReorderableLazyListState(
        onMove = onMove,
        canDragOver = { draggedOver, _ -> onDragOver(draggedOver) },
    )

    val isAddItemEnabled by remember(paymentMethodItems.size) {
        derivedStateOf { paymentMethodItems.size < 10 }
    }

    val handleBack: () -> Unit = {
        if (isDragging) {
            isShowSaveBackDialog = true
        } else {
            onBack()
        }
    }

    BackHandlerWrapper(onBack = handleBack)

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        PaymentMethodManagementTopBar(
            isDragging = isDragging,
            isAddItemEnabled = isAddItemEnabled,
            onBack = handleBack,
            onAddItem = onAddItem
        )

        if (paymentMethodItems.isEmpty()) {
            EmptyListView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Padding.BackgroundHorizontal),
                contentText = "결제 수단을 추가해주세요.",
            )
        } else {
            PaymentMethod(
                listState = listState,
                paymentMethodItems = paymentMethodItems,
                isDragging = isDragging,
                onIsDraggingChange = { isDragging = it },
                onItemClick = onItemClick
            )
        }

        if (isDragging) {
            WMFloatingButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                onClick = {
                    onSaveSort()
                    isDragging = false
                }
            )
        }
        if (isShowSaveBackDialog) {
            WMSaveBackDialog(
                onConfirm = {
                    isShowSaveBackDialog = false
                    isDragging = false
                    onReorderCancel()
                },
                onDismiss = { isShowSaveBackDialog = false }
            )
        }
    }
}

@Composable
private fun PaymentMethodManagementTopBar(
    isDragging: Boolean,
    isAddItemEnabled: Boolean,
    onBack: () -> Unit,
    onAddItem: () -> Unit,
) {
    if (isDragging) {
        WMTopBar(
            title = TopBarItem.Title("${MenuEnum.PAYMENT_METHOD.label} 순서 변경"),
            readingItem = TopBarItem.ReadingItem(action = onBack)
        )
    } else {
        WMTopBar(
            title = TopBarItem.Title(MenuEnum.PAYMENT_METHOD.title),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_add,
                    tint = if (isAddItemEnabled) ColorSetting.Default else ColorSetting.DisabledBackground,
                    action = { if (isAddItemEnabled) onAddItem() }
                )
            )
        )
    }
}

@Preview
@Composable
private fun PaymentMethodManagementContentPreview() {
    WMTheme {
        PaymentMethodManagementContent(
            paymentMethodItems = emptyList(),
            onBack = {},
            onAddItem = {},
            onItemClick = {},
            onSaveSort = {},
            onReorderCancel = {},
            onMove = { _, _ -> },
            onDragOver = { true }
        )
    }
}
