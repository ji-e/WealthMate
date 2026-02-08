package com.jie.wealthmate.feature.menu.data.googleCloudShare

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.vo.GoogleDrivePermissionVo

data class GoogleCloudShareUiState(
    val isLoggedIn: Boolean = false,
    val isOwnerMode: Boolean = false,
    val isGuestMode: Boolean = false,
    val userName: String = "",
    val sharedFolderId: String = "",
    val inviteEmail: TextFieldValue = TextFieldValue(),
    val inviteCode: TextFieldValue = TextFieldValue(),
    val googleDrivePermissionVo: GoogleDrivePermissionVo? = null,
) : BaseUiState {
    val isInitMode = isOwnerMode.not() && isGuestMode.not() && sharedFolderId.isEmpty()
}
