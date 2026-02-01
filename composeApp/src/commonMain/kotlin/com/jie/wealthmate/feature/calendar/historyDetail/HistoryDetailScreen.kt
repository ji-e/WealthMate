@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.calendar.historyDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.topbar.TopBarItem
import org.koin.compose.koinInject

class HistoryDetailScreen(
    val historyId: String,
) : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: HistoryDetailScreenModel = koinInject()
        val uiState by screenModel.container.uiState.collectAsState()

        fun onBack() {
            showSaveBackDialog(uiState.isDataChanged) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is HistoryDetailUiSideEffect.OnSuccessSave -> {
                    navigator.pop()
                }
            }
        }

        LaunchedEffect(uiState.isDataChanged) {
            screenModel.updateTopBar(
                title = TopBarItem.Title("내역 상세"),
                readingItem = TopBarItem.ReadingItem().copy(
                    action = { onBack() }
                ),
                trailingCustomItem =
                    TopBarItem.TrailingCustomItem {
                        val selectedLargeCategoryEnum = uiState.selectedLargeCategory
                        WMText(
                            text = selectedLargeCategoryEnum.label,
                            style = Typography().labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(CircleShape)
                                .background(selectedLargeCategoryEnum.backgroundColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
            )
        }

        LaunchedEffect(Unit) {
            screenModel.updateInit(
                historyId = historyId
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {


                Spacer(modifier = Modifier.height(32.dp))

            }

            // 저장 버튼
            WMFloatingButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.isSaveButtonEnable,
                onClick = screenModel::saveHistory
            )
        }
    }
}
