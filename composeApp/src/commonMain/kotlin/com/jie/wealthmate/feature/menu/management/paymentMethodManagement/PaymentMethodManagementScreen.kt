@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.management.paymentMethodManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.AddPaymentMethodScreen
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.component.PaymentMethod
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod.ModifyPaymentMethodScreen
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class PaymentMethodManagementScreen() : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: PaymentMethodManagementScreenModel = koinInject()
        val uiState by screenModel.container.uiState.collectAsState()

        var isDragging by remember { mutableStateOf(false) }
        val listState = rememberReorderableLazyListState(
            onMove = { from, to ->
                screenModel.handleReorderCategoryItems(
                    from = from.index,
                    to = to.index
                )
            }
        )

        val onBack: () -> Unit = remember(isDragging) {
            {
                if (isDragging) {
                    showSaveBackDialog(
                        isShow = isDragging,
                        callback = { isDragging = false }
                    )
                } else {
                    navigator.pop()
                }
            }
        }

        BackHandler(
            enabled = true,
            onBack = onBack
        )

        if (navigator.lastItem is PaymentMethodManagementScreen) {
            SideEffect {
                if (isDragging) {
                    screenModel.updateTopBar(
                        title = TopBarItem.Title("${MenuEnum.PAYMENT_METHOD.label} 순서 변경"),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { onBack() }
                        )
                    )
                } else {
                    val isAddItemEnabled = uiState.paymentMethodItems.size < 10
                    screenModel.updateTopBar(
                        title = TopBarItem.Title(MenuEnum.PAYMENT_METHOD.title),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { navigator.pop() }
                        ),
                        trailingItem = listOf(
                            TopBarItem.TrailingItem(
                                iconRes = Res.drawable.ic_add,
                                tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                                action = {
                                    if (isAddItemEnabled.not()) return@TrailingItem

                                    navigator.push(
                                        AddPaymentMethodScreen(
                                            paymentMethodItems = uiState.paymentMethodItems
                                        )
                                    )
                                }
                            )
                        )
                    )
                }
            }
        }

        Column {
            if (uiState.paymentMethodItems.isEmpty()) {
                EmptyListView(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    contentText = "결제수단을 추가해주세요.",
                )

                return
            }

            PaymentMethod(
                listState = listState,
                paymentMethodItems = uiState.paymentMethodItems,
                isDragging = isDragging,
                onIsDraggingChange = { isDragging = it },
                onItemClick = {
                    goToModifyPaymentMethod(
                        navigator = navigator,
                        paymentMethodId = it.id
                    )
                }
            )

            // Drag and Drop 저장 버튼
            if (isDragging) {
                WMFloatingButton(
                    text = "저장",
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        screenModel.savePaymentMethodSort()
                        isDragging = false
                    }
                )
            }
        }
    }

    private fun goToModifyPaymentMethod(
        navigator: Navigator,
        paymentMethodId: String,
    ) {
        navigator.push(
            ModifyPaymentMethodScreen(
                paymentMethodId = paymentMethodId
            )
        )
    }


    @Composable
    @Preview(showBackground = true)
    private fun PaymentMethodManagementScreenPreview() {
        WMTheme {
            PaymentMethodManagementScreen()
        }
    }
}