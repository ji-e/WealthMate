@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.googleCloudShare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.googleCloudShare.component.SharedMemberList
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.default

class GoogleCloudShareScreen() : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: GoogleCloudShareScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

        val clipboardManager = LocalClipboardManager.current

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
                label = "연결된 계정",
                value = uiState.userName,
                onValueChange = {},
                readOnlyColor = ColorGray.Gray_400,
                readOnly = true,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 20.dp)
            )

            if (uiState.isOwner) {
                val sharedFolderId = uiState.sharedFolderId

                WMTextField(
                    label = "나의 초대 코드",
                    value = sharedFolderId,
                    onValueChange = {},
                    readOnlyColor = ColorGray.Gray_400,
                    readOnly = true,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(horizontal = 20.dp),
                    onReadOnlyClick = {
                        if (sharedFolderId.isNotEmpty()) {
                            clipboardManager.setText(AnnotatedString(sharedFolderId))
                            screenModel.showSnackbar("초대 코드가 복사되었습니다.")
                        }
                    }
                )

                WMTextField(
                    label = "초대 할 이메일",
                    value = uiState.email,
                    onValueChange = screenModel::updateEmail,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(horizontal = 20.dp),
                    placeholder = "초대 할 구글 이메일을 입력해 주세요."
                )

                WMButton(
                    text = "초대하기",
                    onClick = screenModel::startSharing,
                    enabled = uiState.email.text.isNotBlank(),
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                )


                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 32.dp, bottom = 28.dp)
                        .padding(horizontal = 20.dp),
                    color = ColorGray.Gray_100
                )
            }

            SharedMemberList(
                permissionsItems = uiState.googleDrivePermissionVo?.permissions.default(),
                isOwner = uiState.isOwner,
                onRemoveClick = {}
            )


//
//            WMTextField(
//                label = "초대 받은 코드",
//                value = uiState.code,
//                onValueChange = screenModel::updateCode,
//            )
//
//            WMButton(
//                text = "초대 코드로 연결",
//                onClick = screenModel::connectToSharedFolder
//            )
//
//            WMButton(
//                text = "upload",
//                onClick = { screenModel.uploadMyDataToSharedFolder() }
//            )
//
//            WMButton(
//                text = "download",
//                onClick = { screenModel.syncFromSharedFolder() }
//            )
        }
    }
}
