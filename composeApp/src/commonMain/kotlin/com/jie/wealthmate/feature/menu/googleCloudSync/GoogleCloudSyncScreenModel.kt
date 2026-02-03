package com.jie.wealthmate.feature.menu.googleCloudSync

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.AppDatabase
import com.jie.wealthmate.database.DatabaseManager
import com.jie.wealthmate.repository.DBRepository

class GoogleCloudSyncScreenModel(
    private val dbRepository: DBRepository,
    private val dbManager: DatabaseManager,
    private val appDatabase: AppDatabase,
) : BaseScreenModel<GoogleCloudSyncUiState>() {

    override val initialState: GoogleCloudSyncUiState
        get() = GoogleCloudSyncUiState()


    fun onSyncClick() {
        val accessToken = container.uiState.value.token ?: return
        launchSafe(
            block = {
                // 1. 로컬 DB 파일 읽기 (Checkpoint 포함 권장)
                dbManager.closeDatabase()

                val dbBytes = dbManager.getDatabaseBytes()
                println("업로드할 파일 크기: ${dbBytes?.size} bytes")
                if (dbBytes != null) {
                    // 2. 구글 드라이브에 동기화
                    dbRepository.syncDatabaseToDrive(accessToken, dbBytes)
                }
            }
        ) {

        }
    }


    fun onSyncClick2() {
        val accessToken = container.uiState.value.token ?: return
        launchSafe(
            block = {
                dbRepository.checkAndDownloadBackup(accessToken)
            }
        ) {
        }
    }

    fun getToken(authCode: String?) {
        authCode ?: return

        launchSafe(
            block = {
                dbRepository.fetchAccessToken(authCode)
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

    fun updateToken(token: String) {
        reduceState { state ->
            state.copy(
                token = token
            )
        }
    }
}