package com.jie.wealthmate.feature.menu.googleCloudSync

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.repository.GoogleRepository

class GoogleCloudSyncScreenModel(
    private val googleRepository: GoogleRepository,
    private val syncManager: DatabaseSyncManager,
) : BaseScreenModel<GoogleCloudSyncUiState>() {

    override val initialState: GoogleCloudSyncUiState
        get() = GoogleCloudSyncUiState()

    /**
     * 로컬 데이터를 클라우드에 증분 동기화 방식으로 업로드합니다.
     */
    fun upload() {
        launchSafe(
            block = {
                syncManager.syncToCloud()
                    .onSuccess {
                        showSnackbar(message = "클라우드에 저장되었습니다")
                    }
                    .onFailure { error ->
                        showSnackbar(message = error.message ?: "업로드 실패")
                    }
            }
        ) {}
    }

    /**
     * 클라우드 데이터를 다운로드하여 로컬 DB와 증분 방식으로 병합합니다.
     */
    fun download() {
        launchSafe(
            block = {
                syncManager.syncFromCloud()
                    .onSuccess {
                        showSnackbar(message = "최신 데이터를 불러왔습니다")
                    }
                    .onFailure { error ->
                        showSnackbar(message = error.message ?: "다운로드 실패")
                    }
            }
        ) {}
    }

    fun getToken(authCode: String?, email: String) {
        println(authCode)
        authCode ?: return

        launchSafe(
            block = {
                googleRepository.fetchGoogleAuth(
                    authCode = authCode,
                    email = email
                )
            }
        ) {}
    }

    fun syncFromCloudOnStart() {
        launchSafe(
            block = {
                syncManager.syncFromCloud()
                    .onSuccess {
                        // 조용히 성공
                        println("syncFromCloudOnStart::: success")
                    }
                    .onFailure { _ ->
                        // 실패해도 로컬 데이터로 계속 진행
                        println("syncFromCloudOnStart::: fail")
                    }
            }
        ) {}
    }
}
