@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.entity.GoogleAuthEntity
import com.russhwolf.settings.Settings
import io.github.aakira.napier.Napier
import kotlin.time.ExperimentalTime

// 플랫폼별로 다르게 동작할 silentSignIn의 실제 구현부
expect suspend fun platformSilentSignIn(): GoogleAuthEntity?

class AuthRepositoryImpl(
    private val settings: Settings,
) : AuthRepository {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_SHARED_FOLDER_ID = "shared_folder_id"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_LAST_SHARED_SYNC_TIME = "last_shared_sync_time"
    }

    override fun saveAuthData(accessToken: String, refreshToken: String?, email: String?) {
        settings.putString(KEY_ACCESS_TOKEN, accessToken)
        refreshToken?.let { settings.putString(KEY_REFRESH_TOKEN, it) }
        email?.let { settings.putString(KEY_USER_NAME, it) }
    }

    override fun getAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS_TOKEN)

    override fun isLoggedIn(): Boolean = getAccessToken() != null

    override fun clearAuthData() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_USER_NAME)
    }

    override fun getRefreshToken() = settings.getStringOrNull(KEY_REFRESH_TOKEN)
    override fun getUserName() = settings.getStringOrNull(KEY_USER_NAME)
    override fun saveSharedFolderId(folderId: String) { settings.putString(KEY_SHARED_FOLDER_ID, folderId) }
    override fun getSharedFolderId() = settings.getStringOrNull(KEY_SHARED_FOLDER_ID)

    override fun getDeviceId(): String {
        val savedDeviceId = settings.getStringOrNull(KEY_DEVICE_ID)
        return if (savedDeviceId != null) {
            savedDeviceId
        } else {
            val newDeviceId = uuid4().toString()
            settings.putString(KEY_DEVICE_ID, newDeviceId)
            newDeviceId
        }
    }

    override fun saveLastSyncTime(time: Long) { settings.putLong(KEY_LAST_SYNC_TIME, time) }
    override fun getLastSyncTime(): Long = settings.getLong(KEY_LAST_SYNC_TIME, 0L)
    override fun saveLastSharedSyncTime(time: Long) { settings.putLong(KEY_LAST_SHARED_SYNC_TIME, time) }
    override fun getLastSharedSyncTime(): Long = settings.getLong(KEY_LAST_SHARED_SYNC_TIME, 0L)

    override suspend fun silentSignIn(): GoogleAuthEntity? {
        return try {
            Napier.d("Attempting silent sign-in via platform implementation...")
            val result = platformSilentSignIn()
            if (result != null) {
                // 성공 시 새로운 토큰 저장
                saveAuthData(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken ?: getRefreshToken()
                )
            }
            result
        } catch (e: Exception) {
            Napier.e("Silent sign-in failed", e)
            null
        }
    }
}
