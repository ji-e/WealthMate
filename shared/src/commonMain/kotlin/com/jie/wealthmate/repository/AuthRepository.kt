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

    // 기기 고유 ID 가져오기
    fun getDeviceId(): String

    // 마지막 동기화 시간 관리 (개인 백업용)
    fun saveLastSyncTime(time: Long)
    fun getLastSyncTime(): Long

    // 마지막 공유 폴더 동기화 시간 관리
    fun saveLastSharedSyncTime(time: Long)
    fun getLastSharedSyncTime(): Long
}
