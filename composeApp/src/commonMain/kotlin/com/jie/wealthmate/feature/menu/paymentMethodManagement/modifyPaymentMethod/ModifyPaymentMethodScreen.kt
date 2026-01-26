@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.paymentMethodManagement.modifyPaymentMethod

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.theme.ColorRed
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
                                   // todo 삭제
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
                is ModifyPaymentMethodUiSideEffect.OnSuccessSave -> navigator.pop()
            }
        }
    }


}
