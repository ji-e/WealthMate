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
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.component.PaymentMethod
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.Padding
import org.koin.compose.viewmodel.koinViewModel
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

@Composable
fun PaymentMethodManagementScreen(
    navController: NavController,
    viewModel: PaymentMethodManagementViewModel = koinViewModel(),
) {
    val uiState by viewModel.container.uiState.collectAsState()
    var isDragging by remember { mutableStateOf(false) }

    val listState = rememberReorderableLazyListState(
        onMove = viewModel::handleReorderPaymentMethodItems,
        canDragOver = { draggedOver, _ ->
            viewModel.onDragOver(draggedOver)
        },
    )

    val isAddItemEnabled by remember {
        derivedStateOf { viewModel.paymentMethodItems.size < 30 }
    }

    val onBack: () -> Unit = {
        if (isDragging) {
            isDragging = false
            viewModel.getPaymentMethods()
        } else {
            navController.popBackStack()
        }
    }

    BaseScreen(
        viewModel = viewModel,
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            if (isDragging) {
                WMTopBar(
                    title = TopBarItem.Title("${MenuEnum.PAYMENT_METHOD.label} 순서 변경"),
                    readingItem = TopBarItem.ReadingItem(action = onBack)
                )
            } else {
                WMTopBar(
                    title = TopBarItem.Title(MenuEnum.PAYMENT_METHOD.title),
                    readingItem = TopBarItem.ReadingItem(action = { navController.popBackStack() }),
                    trailingItem = listOf(
                        TopBarItem.TrailingItem(
                            iconRes = Res.drawable.ic_add,
                            tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                            action = {
                                if (isAddItemEnabled) navController.navigate("addPaymentMethod")
                            }
                        )
                    )
                )
            }

            if (viewModel.paymentMethodItems.isEmpty()) {
                EmptyListView(
                    modifier = Modifier.fillMaxSize().padding(Padding.BackgroundHorizontal),
                    contentText = "결제 수단을 추가해주세요.",
                )
            } else {
                PaymentMethod(
                    listState = listState,
                    paymentMethodItems = viewModel.paymentMethodItems,
                    isDragging = isDragging,
                    onIsDraggingChange = { isDragging = it },
                    onItemClick = { navController.navigate("modifyPaymentMethod/${it.id}") }
                )
            }

            if (isDragging) {
                WMFloatingButton(
                    text = "저장",
                    buttonSize = ButtonSize.LARGE,
                    onClick = {
                        viewModel.savePaymentMethodSort()
                        isDragging = false
                    }
                )
            }
        }
    }
}
