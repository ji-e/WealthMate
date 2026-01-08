package com.jie.wealthmate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.bottomNav.BottomNavigation
import com.jie.wealthmate.theme.ColorGray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var selectedItem by remember { mutableStateOf(BottomNavItem.Home.route) }

        Scaffold(
            bottomBar = {
                BottomNavigation(
                    selectedItem = selectedItem,
                    onItemSelected = { selectedItem = it }
                )
            },
            containerColor = ColorGray.White,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var showContent by remember { mutableStateOf(false) }

                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }

                when (selectedItem) {
                    BottomNavItem.Home.route -> {
                        Text(
                            text = "선택된 화면: 홈",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    BottomNavItem.Calendar.route -> {
                        Text(
                            text = "선택된 화면: 캘린더",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    BottomNavItem.Asset.route -> {
                        Text(
                            text = "선택된 화면: 자산",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    BottomNavItem.Menu.route -> {
                        Text(
                            text = "선택된 화면: 메뉴",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }
            }
        }
    }
}
