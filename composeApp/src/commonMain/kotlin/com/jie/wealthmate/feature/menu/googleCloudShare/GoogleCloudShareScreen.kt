@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.googleCloudShare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.textField.WMTextField
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
            WMTextField(
                label = "초대 할 이메일",
                value = uiState.email,
                onValueChange = screenModel::updateEmail,
            )

            WMButton(
                text = "공유",
                onClick = screenModel::startSharing
            )

            WMTextField(
                label = "초대 받은 코드",
                value = uiState.code,
                onValueChange = screenModel::updateCode,
            )

            WMButton(
                text = "초대 코드로 연결",
                onClick = screenModel::connectToSharedFolder
            )


            // db 시각적 표현
            // 현재 구글 드라이브 앱 데이터 폴더에 저장된 DB 파일의 개수를 보여주는 로직입니다.
            // val dbCount = uiState.dbFiles?.files?.size ?: 0
            // Text(
            //     text = "현재 저장된 DB 개수: $dbCount",
            //     modifier = Modifier.padding(16.dp)
            // )

            // 상세 목록을 보여주고 싶은 경우 아래와 같이 구현할 수 있습니다.
            uiState.dbFiles?.files?.forEach { file ->
                Text(
                    text = "파일명: ${file.name} (ID: ${file.id})",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

        }
    }
}
