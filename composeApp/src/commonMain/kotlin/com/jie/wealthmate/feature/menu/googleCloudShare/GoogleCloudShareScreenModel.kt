package com.jie.wealthmate.feature.menu.googleCloudShare

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
                    isOwnerMode = googleDrivePermissionVo.permissions.find { it.emailAddress == state.userName }?.role == "owner"
                )
            }
        }
    }

    fun getToken(authCode: String?, email: String) {
        authCode ?: return

        launchSafe(
            block = {
                googleRepository.fetchGoogleAuth(
                    authCode = authCode,
                    email = email
                )
            }
        ) { response ->
            reduceState { state ->
                state.copy(
                    isLoggedIn = response?.accessToken != null,
                    userName = email
                )
            }
        }
    }

    fun createShareFolder() {
        launchSafe(
            block = {
                val sharedFolderId = googleRepository.createSharedFolder("Wealth_Mate_Shared")
                    ?: throw Exception("잠시 후 다시 시도해 주세요.")

                fetchFilePermissions(sharedFolderId)
                reduceState { state ->
                    state.copy(
                        sharedFolderId = sharedFolderId,
                        isOwnerMode = true
                    )
                }
            }
        )
    }

    /**
     * 1단계: 공유 시작 (호스트 사용자 A)
     */
    fun inviteMember() {
        // todo
        return
        launchSafe(
            block = {
                val folderName = "Wealth_Mate_Shared"
                val folderId = googleRepository.getOrCreateSharedFolder(folderName)
                    ?: throw Exception("공유 폴더 생성 실패")

                val isPermissionGranted =
                    googleRepository.grantPermission(folderId, container.uiState.value.email.text)
                if (!isPermissionGranted) {
                    throw Exception("상대방 권한 부여 실패")
                }

                folderId
            }
        ) { response ->
            reduceState { state ->
                state.copy(
                    sharedFolderId = response,
                    inviteCode = TextFieldValue(response)
                )
            }
        }
    }

    /**
     * 3단계: 게스트(B)의 구현: 폴더 연결
     */
    fun connectToSharedFolder() {
        val folderId = container.uiState.value.inviteCode.text

        launchSafe(
            block = {
                googleRepository.connectToSharedFolder(folderId)
            }
        )
    }

    /**
     * 4단계: 공유 폴더 데이터 업로드 (내 기기 데이터 전송)
     * 파일명 규칙: sync_user_{device_id}.json
     */
    fun uploadMyDataToSharedFolder() {

        launchSafe(
            block = {
                syncManager.syncToSharedFolder()
            }
        ) {
            Napier.d("내 데이터를 공유 폴더에 성공적으로 업로드했습니다.")
            fetchSharedFolderFiles() // 업로드 후 목록 갱신
        }
    }

    /**
     * 4단계: 공유 폴더로부터 데이터 동기화 (상대방 데이터 가져오기)
     */
    fun syncFromSharedFolder() {
        launchSafe(
            block = {
                syncManager.syncFromSharedFolder()
            }
        ) {
            Napier.d("상대방 데이터를 공유 폴더에서 성공적으로 다운로드했습니다.")
        }
    }

    private fun fetchSharedFolderFiles() {
        val folderId = container.uiState.value.sharedFolderId
        launchSafe(
            block = {
                googleRepository.getFilesFromSharedFolder(folderId)
            }
        ) { response ->
            reduceState { state ->
                state.copy(dbFiles = response)
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
            state.copy(email = email)
        }
    }

    fun updateInviteCode(inviteCode: TextFieldValue) {
        reduceState { state ->
            state.copy(inviteCode = inviteCode)
        }
    }
}
