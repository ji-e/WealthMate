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
import com.jie.wealthmate.component.HeadLineText
import com.jie.wealthmate.component.InfoText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categoryManagement.component.GoogleLoginButton
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.googleCloudShare.component.Guest
import com.jie.wealthmate.feature.menu.googleCloudShare.component.Owner
import com.jie.wealthmate.feature.menu.googleCloudShare.component.ShareMethod
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
            HeadLineText(
                text = "구글 드라이브를 통해 가족, 연인과 함께\n가계부를 공유해 보세요.",
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 8.dp)
                    .padding(horizontal = 28.dp)
            )

            InfoText(
                text = "사용자의 데이터를 서버에 저장하지 않고 본인의 구글 드라이브에만 보관합니다.",
                modifier = Modifier.padding(horizontal = 28.dp)
            )

            if (uiState.isLoggedIn) {
                WMTextField(
                    label = "연결된 계정",
                    value = uiState.userName,
                    onValueChange = {},
                    readOnlyColor = ColorGray.Gray_400,
                    readOnly = true,
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(horizontal = 28.dp)
                )

                if (uiState.isOwnerMode) {
                    Owner(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .padding(horizontal = 28.dp),
                        sharedFolderId = uiState.sharedFolderId,
                        email = uiState.inviteEmail,
                        updateEmail = screenModel::updateEmail,
                        onInviteClick = screenModel::inviteMember,
                        showSnackbar = screenModel::showSnackbar,
                    )
                }

                if (uiState.isGuestMode) {
                    Guest(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .padding(horizontal = 28.dp),
                        inviteCode = uiState.inviteCode,
                        onInviteCodeChange = screenModel::updateInviteCode,
                        onInviteClick = screenModel::connectToSharedFolder,
                    )
                }

                if (uiState.isInitMode) {
                    ShareMethod(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .padding(horizontal = 28.dp),
                        onCreateShareFolderClick = screenModel::createShareFolder,
                        onInviteClick = { screenModel.updateIsGuestMode(true) }
                    )
                }

                if (uiState.sharedFolderId.isNotEmpty()) {
                    SharedMemberList(
                        modifier = Modifier.padding(top = 4.dp),
                        permissionsItems = uiState.googleDrivePermissionVo?.permissions.default(),
                        isOwner = uiState.isOwnerMode,
                        onRemoveClick = {},
                        emptyContent = {
                            EmptyListView(
                                modifier = Modifier.fillMaxSize()
                                    .padding(vertical = 20.dp, horizontal = 28.dp),
                                contentText = "공유된 멤버가 없습니다.",
                            )
                        }
                    )
                }
            } else {
                GoogleLoginButton(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .padding(top = 32.dp)
                ) { googleUser ->
                    screenModel.getToken(
                        authCode = googleUser.serverAuthCode,
                        email = googleUser.email.default()
                    )
                }
            }
        }
    }
}
