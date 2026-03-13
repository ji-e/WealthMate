package com.jie.wealthmate.feature.budget.budgetYearDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.theme.ColorGray
import org.koin.core.parameter.parametersOf

class BudgetYearDetailScreen(
    private val selectedYear: String,
) : BaseScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: BudgetYearDetailScreenModel = koinScreenModel { parametersOf(selectedYear) }
        val uiState by screenModel.container.uiState.collectAsState()

        Column(modifier = Modifier.fillMaxSize().background(ColorGray.White)) {
            WMTopBar(
                title = TopBarItem.Title("${selectedYear}년 예산 상세"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
                )
            )
        }
    }
}
