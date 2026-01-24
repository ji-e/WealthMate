@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.paymentMethodManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.component.topbar.TopBarItem
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
        val uiState = screenModel.container.uiState.collectAsState().value

        var isDragging by remember { mutableStateOf(false) }
        val listState = rememberReorderableLazyListState(
            onMove = { from, to -> screenModel.handleReorderCategoryItems(from.index, to.index) }
        )

        fun onBack() {
            if (isDragging) {
                showSaveBackDialog(isDragging) {
                    isDragging = false
                    // todo 새로고침
                }
            } else {
                navigator.pop()
            }
        }

        BackHandler(true) {
            onBack()
        }

        if (navigator.lastItem is PaymentMethodManagementScreen) {
            SideEffect {
                if (isDragging) {
                    screenModel.updateTopBar(
                        title = TopBarItem.Title("결제수단 순서 변경"),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { onBack() }
                        )
                    )
                } else {
                    val isAddItemEnabled = uiState.paymentMethodItems.size < 10
                    screenModel.updateTopBar(
                        title = TopBarItem.Title(
                            "결제수단 관리"
                        ),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { navigator.pop() }
                        ),
                        trailingItem = listOf(
                            TopBarItem.TrailingItem(
                                iconRes = Res.drawable.ic_add,
                                tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                                action = {
                                    if (isAddItemEnabled.not()) return@TrailingItem

                                    // todo 결제수단 추가 화면 이동
//                                    navigator.push(
//                                        AddCategoryScreen(
//                                            largeCategory = largeCategoryItems[pagerState.currentPage],
//                                            categoryItems = uiState.paymentMethodItems
//                                        )
//                                    )
                                }
                            )
                        )
                    )
                }
            }
        }

        Column {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .reorderable(listState),
                state = listState.listState
            ) {
                // todo 결제수단 리스트

            }
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
                        // todo 저장
                        isDragging = false
                    }
                )
            }
        }
    }


    @Composable
    @Preview(showBackground = true)
    private fun PaymentMethodManagementScreenPreview() {
        WMTheme {
            PaymentMethodManagementScreen()
        }
    }
}