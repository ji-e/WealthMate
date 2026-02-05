@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.googleCloudShare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.googleCloudShare.component.Owner
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
                Owner(
                    sharedFolderId = uiState.sharedFolderId,
                    email = uiState.email,
                    updateEmail = screenModel::updateEmail,
                    onInviteClick = screenModel::inviteMember,
                    showSnackbar = screenModel::showSnackbar,
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
