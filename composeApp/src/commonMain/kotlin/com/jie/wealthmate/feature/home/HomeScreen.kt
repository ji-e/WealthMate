package com.jie.wealthmate.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.WMText
import org.koin.compose.koinInject

class HomeScreen(val calculateBottomPadding: Dp) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: HomeScreenModel = koinInject()
        val counter by screenModel.counter.collectAsState()

        if (navigator.lastItem is HomeScreen) {
            SideEffect {
                screenModel.updateTopBar()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom =calculateBottomPadding ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { screenModel.increment() }) {
                WMText(text = "Increment $counter", style = Typography().bodyLarge)
            }
        }
    }
}