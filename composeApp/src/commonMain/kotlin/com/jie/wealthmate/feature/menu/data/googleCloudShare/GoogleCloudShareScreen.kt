package com.jie.wealthmate.feature.menu.data.googleCloudShare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.HeadLineText
import com.jie.wealthmate.component.InfoText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.component.GoogleLoginButton
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.feature.menu.data.googleCloudShare.component.Guest
import com.jie.wealthmate.feature.menu.data.googleCloudShare.component.Owner
import com.jie.wealthmate.feature.menu.data.googleCloudShare.component.ShareMethod
import com.jie.wealthmate.feature.menu.data.googleCloudShare.component.SharedMemberList
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.utils.default
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GoogleCloudShareScreen(
    navController: NavController,
    viewModel: GoogleCloudShareViewModel = koinViewModel(),
) {
    val uiState by viewModel.container.uiState.collectAsState()

    BaseScreen(viewModel = viewModel) {
        GoogleCloudShareContent(
            uiState = uiState,
            onBack = {
                if (uiState.isGuestMode) {
                    viewModel.updateIsGuestMode(false)
                } else {
                    navController.popBackStack()
                }
            },
            onUpdateEmail = viewModel::updateEmail,
            onInviteMember = viewModel::inviteMember,
            onUpdateInviteCode = viewModel::updateInviteCode,
            onConnectToSharedFolder = viewModel::connectToSharedFolder,
            onCreateShareFolder = viewModel::createShareFolder,
            onUpdateIsGuestMode = viewModel::updateIsGuestMode,
            onUpdateUser = viewModel::updateUser,
            onShowSnackbar = viewModel::showSnackbar
        )
    }
}

@Composable
fun GoogleCloudShareContent(
    uiState: GoogleCloudShareUiState,
    onBack: () -> Unit,
    onUpdateEmail: (TextFieldValue) -> Unit,
    onInviteMember: () -> Unit,
    onUpdateInviteCode: (TextFieldValue) -> Unit,
    onConnectToSharedFolder: () -> Unit,
    onCreateShareFolder: () -> Unit,
    onUpdateIsGuestMode: (Boolean) -> Unit,
    onUpdateUser: (String?, String) -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        WMTopBar(
            title = TopBarItem.Title(MenuEnum.GOOGLE_SHARE.label),
            readingItem = TopBarItem.ReadingItem(action = onBack),
        )

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
                    updateEmail = onUpdateEmail,
                    onInviteClick = onInviteMember,
                    showSnackbar = onShowSnackbar,
                )
            }

            if (uiState.isGuestMode) {
                Guest(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(horizontal = 28.dp),
                    inviteCode = uiState.inviteCode,
                    onInviteCodeChange = onUpdateInviteCode,
                    onInviteClick = onConnectToSharedFolder,
                )
            }

            if (uiState.isInitMode) {
                ShareMethod(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 28.dp),
                    onCreateShareFolderClick = onCreateShareFolder,
                    onInviteClick = { onUpdateIsGuestMode(true) }
                )
            }

            if (uiState.sharedFolderId.isNotEmpty()) {
                SharedMemberList(
                    modifier = Modifier.padding(top = 4.dp),
                    permissionsItems = uiState.googleDrivePermissionVo?.permissions.default(),
                    isOwner = uiState.isOwnerMode,
                    onRemoveClick = {},
                )
            }
        } else {
            GoogleLoginButton(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 32.dp)
            ) { googleUser ->
                onUpdateUser(
                    googleUser.accessToken,
                    googleUser.email.default()
                )
            }
        }
    }
}
