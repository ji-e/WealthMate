@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.budget.addBudget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar


class AddBudgetScreen() : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddBudgetScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

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

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            WMTopBar(
                title = TopBarItem.Title("예산 추가"),
                readingItem = TopBarItem.ReadingItem().copy(action = { onBack() }),
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

            }
        }
    }
}