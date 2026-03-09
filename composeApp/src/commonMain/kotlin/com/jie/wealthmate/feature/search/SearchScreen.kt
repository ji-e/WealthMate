package com.jie.wealthmate.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.component.textField.WMSearchTextField

class SearchScreen : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: SearchScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        Column(modifier = Modifier.fillMaxSize()) {
            WMTopBar(
                title = TopBarItem.Title("내역 검색"),
                readingItem = TopBarItem.ReadingItem().copy(action = { navigator.pop() }),
            )

            WMSearchTextField(
                modifier = Modifier
                    .padding(start = 28.dp, end = 16.dp)
                    .padding(top = 4.dp),
                value = uiState.query,
                onValueChange = screenModel::updateQuery,
                onSearch = { screenModel.search() },
                onClear = { screenModel.clearQuery() },
                placeholder = "검색할 카테고리, 내용을 입력하세요"
            )

            // TODO: Implement search results list
        }
    }
}
