package com.jie.wealthmate.feature.menu.googleCloudShare

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.entity.GoogleDriveFileEntity
import com.jie.wealthmate.vo.GoogleDrivePermissionVo

data class GoogleCloudShareUiState(
    val isLoggedIn: Boolean = false,
    val isOwner: Boolean = false,
    val userName: String = "",
    val sharedFolderId: String = "",
    val email: TextFieldValue = TextFieldValue(),
    val code: TextFieldValue = TextFieldValue(),
    val dbFiles: GoogleDriveFileEntity? = null,
    val googleDrivePermissionVo: GoogleDrivePermissionVo? = null,
    val inviteCode: String? = null,
) : BaseUiState
