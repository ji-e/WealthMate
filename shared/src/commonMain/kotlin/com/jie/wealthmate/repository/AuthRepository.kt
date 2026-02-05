package com.jie.wealthmate.repository

interface AuthRepository {

    fun clearAuthData()
    fun isLoggedIn(): Boolean
    fun getAccessToken(): String?
    fun saveAuthData(accessToken: String, refreshToken: String?)
    fun getRefreshToken(): String?

    // 공유 폴더 ID 관리
    fun saveSharedFolderId(folderId: String)
    fun getSharedFolderId(): String?
}
