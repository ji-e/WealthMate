package com.jie.wealthmate.feature.budget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.EmptyListView
import com.jie.wealthmate.component.calculateAdjustedToastPadding
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.budgetManagement.BudgetManagementScreen
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_setting


class BudgetScreen() : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: BudgetScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = calculateAdjustedToastPadding(80)),
        ) {
            WMTopBar(
                title = TopBarItem.Title("예산"),
                readingItem = null,
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_setting,
                        action = { navigator.push(BudgetManagementScreen()) }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                EmptyListView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentText = "설정된 예산이 없습니다."
                )
            }
        }
    }
}