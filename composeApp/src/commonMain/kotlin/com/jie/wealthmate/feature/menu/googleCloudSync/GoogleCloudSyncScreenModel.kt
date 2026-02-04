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

    init {
//        syncFromCloudOnStart()
    }


    fun upload() {
        val accessToken = container.uiState.value.token ?: return
        launchSafe(
            block = {
                syncManager.syncToCloud(accessToken)
                    .onSuccess {
                        showSnackbar("클라우드에 저장되었습니다")
                    }
                    .onFailure { error ->
                        showSnackbar(error.message ?: "업로드 실패")

                    }
            }
        ) {

        }
    }


    fun download() {
        val accessToken = container.uiState.value.token ?: return
        launchSafe(
            block = {
                syncManager.syncFromCloud(accessToken)
                    .onSuccess {
                        showSnackbar("최신 데이터를 불러왔습니다")
                    }
                    .onFailure { error ->
                        showSnackbar(error.message ?: "다운로드 실패")
                    }
            }
        ) {
        }
    }

    fun getToken(authCode: String?) {
        authCode ?: return

        launchSafe(
            block = {
                googleRepository.fetchAccessToken(authCode)
            }
        ) {
            println("accessToken:::: $it")
            reduceState { state ->
                state.copy(
                    token = it
                )
            }
        }
    }

    private fun syncFromCloudOnStart() {
        val accessToken = container.uiState.value.token ?: return
        launchSafe(
            block = {
                syncManager.syncFromCloud(accessToken)
                    .onSuccess {
                        // 조용히 성공
                    }
                    .onFailure { error ->
                        // 실패해도 로컬 데이터로 계속 진행
                        showSnackbar("Auto sync failed:: $error")
                    }
            }
        ) {
        }

    }
}