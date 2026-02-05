@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

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
    }


    fun clear() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    // 1. 토큰 저장
    override fun saveAuthData(accessToken: String, refreshToken: String?) {
        settings.putString(KEY_ACCESS_TOKEN, accessToken)
        refreshToken?.let { settings.putString(KEY_REFRESH_TOKEN, it) }
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

    override fun saveSharedFolderId(folderId: String) {
        settings.putString(KEY_SHARED_FOLDER_ID, folderId)
    }

    override fun getSharedFolderId() = settings.getStringOrNull(KEY_SHARED_FOLDER_ID)

}

