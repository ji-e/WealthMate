package com.jie.wealthmate.feature.menu.data.googleCloudShare

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.GoogleDrivePermissionVo.Companion.mapperToVo
import io.github.aakira.napier.Napier

class GoogleCloudShareScreenModel(
    private val googleRepository: GoogleRepository,
    private val authRepository: AuthRepository,
    private val syncManager: DatabaseSyncManager,
) : BaseScreenModel<GoogleCloudShareUiState>() {

    override val initialState: GoogleCloudShareUiState
        get() = GoogleCloudShareUiState(
            isLoggedIn = authRepository.isLoggedIn()
        )

    init {
        if (authRepository.isLoggedIn()) {
            getUserName()
            if (authRepository.getSharedFolderId().isNullOrEmpty().not()) {
                getSharedFolderId()
            }
        }
    }

    private fun getUserName() {
        if (authRepository.isLoggedIn()) {
            reduceState { state ->
                state.copy(
                    userName = authRepository.getUserName().default()
                )
            }
        }
    }

    private fun getSharedFolderId() {
        launchSafe(
            block = {
                googleRepository.findFolderByName("Wealth_Mate_Shared")
            }
        ) { response ->

            val sharedFolderId =
                authRepository.getSharedFolderId().default().ifEmpty { response.default() }

            fetchFilePermissions(sharedFolderId)

            reduceState { state ->
                state.copy(
                    sharedFolderId = sharedFolderId
                )
            }
        }
    }

    private fun fetchFilePermissions(sharedFolderId: String) {
        launchSafe(
            block = {
                googleRepository.getFilePermissions(sharedFolderId)
            }
        ) { response ->
            val googleDrivePermissionVo = response.mapperToVo()

            reduceState { state ->
                state.copy(
                    googleDrivePermissionVo = googleDrivePermissionVo,
                    isOwnerMode = googleDrivePermissionVo.permissions
                        .find { it.emailAddress.equals(state.userName, ignoreCase = true) }
                        ?.role == "owner"
                )
            }
        }
    }

    fun updateUser(accessToken: String?, email: String) {
        authRepository.saveAuthData(accessToken.default(), null, email)

        reduceState { state ->
            state.copy(
                isLoggedIn = accessToken != null,
                userName = email
            )
        }
    }


    fun createShareFolder() {
        launchSafe(
            block = {
                val sharedFolderId = googleRepository.getOrCreateSharedFolder("Wealth_Mate_Shared")
                    ?: throw Exception("잠시 후 다시 시도해 주세요.")

                fetchFilePermissions(sharedFolderId)
                reduceState { state ->
                    state.copy(
                        sharedFolderId = sharedFolderId,
                        isOwnerMode = true
                    )
                }
            }
        ) {
            uploadMyDataToSharedFolder()
        }
    }

    fun uploadMyDataToSharedFolder() {
        launchSafe(
            block = {
                syncManager.syncFullToSharedFolder()
            }
        ) {
            Napier.d("내 데이터를 공유 폴더에 성공적으로 업로드했습니다.")
        }
    }

    fun inviteMember() {
        val uiState = container.uiState.value
        launchSafe(
            block = {
                val isPermissionGranted =
                    googleRepository.grantPermission(
                        fileId = uiState.sharedFolderId,
                        email = container.uiState.value.inviteEmail.text
                    )
                if (!isPermissionGranted) {
                    throw Exception("잠시 후 다시 시도해 주세요.")
                }
            }
        ) {
            fetchFilePermissions(uiState.sharedFolderId)
            reduceState { state ->
                state.copy(
                    inviteEmail = TextFieldValue("")
                )
            }
        }
    }

    fun connectToSharedFolder() {
        val folderId = container.uiState.value.inviteCode.text

        launchSafe(
            block = {
                googleRepository.connectToSharedFolder(folderId)
            }
        ) {
            reduceState { state ->
                state.copy(
                    sharedFolderId = folderId
                )
            }
            syncFromSharedFolder()
        }
    }

    fun syncFromSharedFolder() {
        launchSafe(
            block = {
                syncManager.syncFromSharedFolder()
            }
        ) {
            Napier.d("상대방 데이터를 공유 폴더에서 성공적으로 다운로드했습니다.")
        }
    }

    fun updateIsGuestMode(isGuestMode: Boolean) {
        reduceState { state ->
            state.copy(isGuestMode = isGuestMode)
        }
    }

    fun updateEmail(email: TextFieldValue) {
        reduceState { state ->
            state.copy(inviteEmail = email)
        }
    }

    fun updateInviteCode(inviteCode: TextFieldValue) {
        reduceState { state ->
            state.copy(inviteCode = inviteCode)
        }
    }
}
