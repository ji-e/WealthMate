@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.russhwolf.settings.Settings
import kotlin.time.ExperimentalTime

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


    fun clear() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    // 1. 토큰 저장
    override fun saveAuthData(accessToken: String, refreshToken: String?, email: String?) {
        settings.putString(KEY_ACCESS_TOKEN, accessToken)
        refreshToken?.let { settings.putString(KEY_REFRESH_TOKEN, it) }
        email?.let { settings.putString(KEY_USER_NAME, it) }

    }

    // 2. 토큰 가져오기
    override fun getAccessToken(): String? {
        val token = settings.getStringOrNull(KEY_ACCESS_TOKEN)
        println("token::: $token")
        return token
    }


    // 3. 로그인 여부 확인
    override fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }

    // 4. 로그아웃 (토큰 삭제)
    override fun clearAuthData() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_USER_NAME)
    }

    override fun getRefreshToken() = settings.getStringOrNull(KEY_REFRESH_TOKEN)

    override fun getUserName() = settings.getStringOrNull(KEY_USER_NAME)


    override fun saveSharedFolderId(folderId: String) {
        settings.putString(KEY_SHARED_FOLDER_ID, folderId)
    }

    override fun getSharedFolderId() = settings.getStringOrNull(KEY_SHARED_FOLDER_ID)

    /**
     * 기기 고유 ID를 가져옵니다.
     * 앱 최초 실행 시 UUID를 생성하여 Settings에 저장하고, 이후에는 저장된 값을 반환합니다.
     */
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

    override fun saveLastSyncTime(time: Long) {
        settings.putLong(KEY_LAST_SYNC_TIME, time)
    }

    override fun getLastSyncTime(): Long {
        return settings.getLong(KEY_LAST_SYNC_TIME, 0L)
    }

    override fun saveLastSharedSyncTime(time: Long) {
        settings.putLong(KEY_LAST_SHARED_SYNC_TIME, time)
    }

    override fun getLastSharedSyncTime(): Long {
        return settings.getLong(KEY_LAST_SHARED_SYNC_TIME, 0L)
    }

}
