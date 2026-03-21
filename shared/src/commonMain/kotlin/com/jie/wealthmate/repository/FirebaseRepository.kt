package com.jie.wealthmate.repository

interface FirebaseRepository {
    suspend fun saveUserConnection(userId: String, identificationKey: String, fcmToken: String? = null)
    suspend fun getFcmToken(): String?
}
