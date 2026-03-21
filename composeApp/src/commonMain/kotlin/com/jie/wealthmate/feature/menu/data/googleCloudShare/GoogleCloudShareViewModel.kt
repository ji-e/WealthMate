package com.jie.wealthmate.feature.menu.data.googleCloudShare

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.GoogleDrivePermissionVo.Companion.mapperToVo
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class GoogleCloudShareViewModel(
    private val googleRepository: GoogleRepository,
    private val authRepository: AuthRepository,
    private val syncManager: DatabaseSyncManager,
) : BaseViewModel<GoogleCloudShareUiState>() {

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
        viewModelScope.launch {
            try {
                val response = googleRepository.findFolderByName("Wealth_Mate_Shared")
                val sharedFolderId =
                    authRepository.getSharedFolderId().default().ifEmpty { response.default() }

                fetchFilePermissions(sharedFolderId)

                reduceState { state ->
                    state.copy(sharedFolderId = sharedFolderId)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun fetchFilePermissions(sharedFolderId: String) {
        viewModelScope.launch {
            try {
                val response = googleRepository.getFilePermissions(sharedFolderId)
                val googleDrivePermissionVo = response.mapperToVo()

                reduceState { state ->
                    state.copy(
                        googleDrivePermissionVo = googleDrivePermissionVo,
                        isOwnerMode = googleDrivePermissionVo.permissions
                            .find { it.emailAddress.equals(state.userName, ignoreCase = true) }
                            ?.role == "owner"
                    )
                }
            } catch (e: Exception) {
                // Handle error
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
        viewModelScope.launch {
            showLoading(true)
            try {
                val sharedFolderId = googleRepository.getOrCreateSharedFolder("Wealth_Mate_Shared")
                    ?: throw Exception("잠시 후 다시 시도해 주세요.")

                fetchFilePermissions(sharedFolderId)
                reduceState { state ->
                    state.copy(
                        sharedFolderId = sharedFolderId,
                        isOwnerMode = true
                    )
                }
                uploadMyDataToSharedFolder()
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun uploadMyDataToSharedFolder() {
        viewModelScope.launch {
            try {
                syncManager.syncFullToSharedFolder()
                Napier.d("내 데이터를 공유 폴더에 성공적으로 업로드했습니다.")
            } catch (e: Exception) {
                Napier.e("내 데이터 업로드 실패: ${e.message}")
            }
        }
    }

    fun inviteMember() {
        val uiState = container.uiState.value
        viewModelScope.launch {
            showLoading(true)
            try {
                val isPermissionGranted = googleRepository.grantPermission(
                    fileId = uiState.sharedFolderId,
                    email = uiState.inviteEmail.text
                )
                if (!isPermissionGranted) {
                    throw Exception("잠시 후 다시 시도해 주세요.")
                }
                fetchFilePermissions(uiState.sharedFolderId)
                reduceState { state ->
                    state.copy(inviteEmail = TextFieldValue(""))
                }
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun connectToSharedFolder() {
        val folderId = container.uiState.value.inviteCode.text
        viewModelScope.launch {
            showLoading(true)
            try {
                googleRepository.connectToSharedFolder(folderId)
                reduceState { state ->
                    state.copy(sharedFolderId = folderId)
                }
                syncFromSharedFolder()
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun syncFromSharedFolder() {
        viewModelScope.launch {
            try {
                syncManager.syncFromSharedFolder()
                Napier.d("상대방 데이터를 공유 폴더에서 성공적으로 다운로드했습니다.")
            } catch (e: Exception) {
                Napier.e("데이터 다운로드 실패: ${e.message}")
            }
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
