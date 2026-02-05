package com.jie.wealthmate.feature.menu.googleCloudShare

import GoogleDriveFileEntity
import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState

data class GoogleCloudShareUiState(
    val token: String? = null,
    val email: TextFieldValue = TextFieldValue(),
    val code: TextFieldValue = TextFieldValue(),
    val dbFiles: GoogleDriveFileEntity? = null,
    val sharedFolderId: String? = null,
    val inviteCode: String? = null,
) : BaseUiState
