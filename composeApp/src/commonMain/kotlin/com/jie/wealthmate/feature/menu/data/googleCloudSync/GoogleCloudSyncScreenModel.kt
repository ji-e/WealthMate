package com.jie.wealthmate.feature.menu.data.googleCloudSync

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatDateDotYYYYMDE
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GoogleCloudSyncScreenModel(
    private val googleRepository: GoogleRepository,
    private val authRepository: AuthRepository,
    private val syncManager: DatabaseSyncManager,
) : BaseScreenModel<GoogleCloudSyncUiState>() {

    override val initialState: GoogleCloudSyncUiState
        get() = GoogleCloudSyncUiState(
            isLoggedIn = authRepository.isLoggedIn()
        )

    init {
        if (authRepository.isLoggedIn()) {
            getUserName()
            getLastSyncTime()
        }
    }

    private fun getUserName() {
        reduceState { state ->
            state.copy(
                userName = authRepository.getUserName().default()
            )
        }
    }

    fun getLastSyncTime() {
        launchSafe(
            block = {
                val fileList = googleRepository.getFileList()
                val latestModifiedTime = fileList?.files
                    ?.mapNotNull { it.modifiedTime }
                    ?.maxOrNull()

                reduceState { state ->
                    state.copy(
                        lastSyncDate = if (latestModifiedTime != null) {
                            try {
                                // RFC 3339 포맷 파싱 (예: 2023-10-27T10:00:00.000Z)
                                val instant = Instant.parse(latestModifiedTime)
                                val localDateTime =
                                    instant.toLocalDateTime(TimeZone.currentSystemDefault())
                                "${localDateTime.date.convertLocalDateToString(formatDateDotYYYYMDE)} ${
                                    localDateTime.time.hour.toString().padStart(2, '0')
                                }:${localDateTime.time.minute.toString().padStart(2, '0')}"
                            } catch (e: Exception) {
                                latestModifiedTime
                            }
                        } else {
                            val lastSyncTime = authRepository.getLastSyncTime()
                            if (lastSyncTime > 0) lastSyncTime.toLocalDate().toString() else "없음"
                        }
                    )
                }
            }
        ) {}
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

    /**
     * 로컬 데이터를 클라우드에 증분 동기화 방식으로 업로드합니다.
     */
    fun upload() {
        launchSafe(
            block = {
                syncManager.syncFullToCloud()
                    .onSuccess {
                        getLastSyncTime() // 업로드 후 시간 갱신
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
                        getLastSyncTime() // 다운로드 후 시간 갱신
                        showSnackbar(message = "최신 데이터를 불러왔습니다")
                    }
                    .onFailure { error ->
                        showSnackbar(message = error.message ?: "다운로드 실패")
                    }
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
