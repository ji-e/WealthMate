@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.feature.menu.data.googleCloudSync

import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.utils.convertLocalDateToString
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatDateDotYYYYMDE
import com.jie.wealthmate.utils.toLocalDate
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GoogleCloudSyncViewModel(
    private val googleRepository: GoogleRepository,
    private val authRepository: AuthRepository,
    private val syncManager: DatabaseSyncManager,
    private val accountProvider: AccountProvider
) : BaseViewModel<GoogleCloudSyncUiState>() {

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
        viewModelScope.launch {
            try {
                val fileList = googleRepository.getFileList()
                val latestModifiedTime = fileList?.files
                    ?.mapNotNull { it.modifiedTime }
                    ?.maxOrNull()

                reduceState { state ->
                    state.copy(
                        lastSyncDate = if (latestModifiedTime != null) {
                            try {
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
            } catch (e: Exception) {
                // Handle or log error
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

    fun upload() {
        viewModelScope.launch {
            showLoading(true)
            syncManager.syncFullToCloud()
                .onSuccess {
                    getLastSyncTime()
                    showSnackbar(message = "클라우드에 저장되었습니다")
                }
                .onFailure { error ->
                    showSnackbar(message = error.message ?: "업로드 실패")
                }
            showLoading(false)
        }
    }

    fun download() {
        viewModelScope.launch {
            showLoading(true)
            if(accountProvider.isSpecialAccount()){
                syncManager.restoreFromFirestore()
                    .onSuccess {
                        getLastSyncTime()
                        showSnackbar(message = "최신 데이터를 불러왔습니다")
                    }
                    .onFailure { error ->
                        showSnackbar(message = error.message ?: "다운로드 실패")
                    }
            }else {
                syncManager.syncFromCloud()
                    .onSuccess {
                        getLastSyncTime()
                        showSnackbar(message = "최신 데이터를 불러왔습니다")
                    }
                    .onFailure { error ->
                        showSnackbar(message = error.message ?: "다운로드 실패")
                    }
            }
            showLoading(false)
        }
    }
}
