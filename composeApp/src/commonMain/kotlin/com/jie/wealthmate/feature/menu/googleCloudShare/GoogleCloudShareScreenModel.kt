package com.jie.wealthmate.feature.menu.googleCloudShare

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.repository.GoogleRepository
import io.github.aakira.napier.Napier

class GoogleCloudShareScreenModel(
    private val googleRepository: GoogleRepository,
    private val syncManager: DatabaseSyncManager,
) : BaseScreenModel<GoogleCloudShareUiState>() {

    override val initialState: GoogleCloudShareUiState
        get() = GoogleCloudShareUiState()

    init {
        fetchDbFiles()
    }

    private fun fetchDbFiles() {
        launchSafe(
            block = {
                googleRepository.getFileList()
            }
        ) { response ->
            reduceState { state ->
                state.copy(dbFiles = response)
            }
        }
    }

    /**
     * 1단계: 공유 시작 (호스트 사용자 A)
     */
    fun startSharing() {
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
                    inviteCode = response
                )
            }
        }
    }

    /**
     * 3단계: 게스트(B)의 구현: 폴더 연결
     */
    fun connectToSharedFolder() {
        val folderId = container.uiState.value.code.text
        if (folderId.isBlank()) return

        launchSafe(
            block = {
                googleRepository.connectToSharedFolder(folderId)
            }
        ) { response ->
            reduceState { state ->
                state.copy(
                    sharedFolderId = folderId,
                    dbFiles = response
                )
            }
        }
    }

    /**
     * 4단계: 공유 폴더 데이터 업로드 (내 기기 데이터 전송)
     * 파일명 규칙: sync_user_{device_id}.json
     */
    fun uploadMyDataToSharedFolder(deviceId: String) {
        val folderId = container.uiState.value.sharedFolderId ?: return
        val fileName = "sync_user_$deviceId.json"

        launchSafe(
            block = {
                syncManager.syncToSharedFolder(folderId, deviceId)
            }
        ) {
            Napier.d("내 데이터를 공유 폴더에 성공적으로 업로드했습니다.")
            fetchSharedFolderFiles() // 업로드 후 목록 갱신
        }
    }

    /**
     * 4단계: 공유 폴더로부터 데이터 동기화 (상대방 데이터 가져오기)
     */
    fun syncFromSharedFolder(myDeviceId: String) {
        val folderId = container.uiState.value.sharedFolderId ?: return

        launchSafe(
            block = {
               syncManager.syncFromSharedFolder(folderId, myDeviceId)
            }
        ) { results ->
            Napier.d("상대방 데이터를 공유 폴더에서 성공적으로 다운로드했습니다.")

        }
    }

    private fun fetchSharedFolderFiles() {
        val folderId = container.uiState.value.sharedFolderId ?: return
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

    fun updateEmail(email: TextFieldValue) {
        reduceState { state ->
            state.copy(email = email)
        }
    }

    fun updateCode(code: TextFieldValue) {
        reduceState { state ->
            state.copy(code = code)
        }
    }
}
