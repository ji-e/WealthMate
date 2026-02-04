package com.jie.wealthmate.repository

interface AuthRepository {

    fun clearAuthData()
    fun isLoggedIn(): Boolean
    fun getAccessToken(): String?
    fun saveAuthData(accessToken: String, refreshToken: String?)
    fun getRefreshToken(): String?
}