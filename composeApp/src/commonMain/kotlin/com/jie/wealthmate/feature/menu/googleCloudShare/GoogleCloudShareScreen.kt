@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.googleCloudShare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.component.MenuEnum

class GoogleCloudShareScreen() : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: GoogleCloudShareScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()


        LaunchedEffect(Unit) {
            screenModel.updateTopBar(
                title = TopBarItem.Title(MenuEnum.GOOGLE_SHARE.label),
                readingItem = TopBarItem.ReadingItem().copy(
                    action = { navigator.pop() }
                ),
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {


        }
    }
}