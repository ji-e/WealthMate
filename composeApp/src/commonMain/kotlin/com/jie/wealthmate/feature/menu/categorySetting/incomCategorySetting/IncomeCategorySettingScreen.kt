@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.component.WMText

class IncomeCategorySettingScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { IncomeCategorySettingScreenModel() }
        val counter by screenModel.counter.collectAsState()

        BackHandler(true) {
            println(navigator.size)
            navigator.pop()
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WMText(
                text = "IncomeCategorySetting Screen Counter: $counter",
                style = Typography().bodyLarge
            )
            Button(onClick = { screenModel.increment() }) {
                WMText(text = "Increment", style = Typography().bodyLarge)
            }
        }
    }
}