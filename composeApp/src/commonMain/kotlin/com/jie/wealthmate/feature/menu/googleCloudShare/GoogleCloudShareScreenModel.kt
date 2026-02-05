package com.jie.wealthmate.feature.menu.googleCloudShare

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.GoogleRepository

class GoogleCloudShareScreenModel(
    private val googleRepository: GoogleRepository,
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
     * 사용자 A가 가계부 데이터를 공유할 '공간'을 만드는 단계입니다.
     */
    fun startSharing() {
        launchSafe(
            block = {
                // [공유 폴더 생성]
                // 앱이 사용자 A의 구글 드라이브에 특정 이름의 폴더를 생성합니다.
                val folderName = "Wealth_Mate_Shared"
                val folderId = googleRepository.createSharedFolder(folderName)
                    ?: throw Exception("공유 폴더 생성 실패")

                // [상대방 초대 및 권한 부여]
                // Google Drive Permission API를 호출하여 사용자 B에게 '편집자(writer)' 권한을 부여합니다.
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

    fun updateEmail(email: TextFieldValue) {
        reduceState { state ->
            state.copy(email = email)
        }
    }
}
