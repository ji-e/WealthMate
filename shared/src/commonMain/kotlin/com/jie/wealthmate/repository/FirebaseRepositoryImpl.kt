package com.jie.wealthmate.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.messaging.messaging
import io.github.aakira.napier.Napier
import kotlin.time.Clock

class FirebaseRepositoryImpl : FirebaseRepository {
    private val firestore = Firebase.firestore
    private val messaging = Firebase.messaging

    override suspend fun saveUserConnection(userId: String, identificationKey: String, fcmToken: String?) {
        if (userId.isBlank()) return

        try {
            val userRef = firestore.collection("users").document(userId)
            val userSnapshot = userRef.get()
            val now = Clock.System.now().toEpochMilliseconds()

            val data = mutableMapOf<String, Any?>(
                "userId" to userId,
                "identificationKey" to identificationKey,
                "updatedAt" to now
            )
            fcmToken?.let { data["fcmToken"] = it }

            // 신규 사용자인 경우에만 createdAt 추가
            if (!userSnapshot.exists) {
                data["createdAt"] = now
            }

            // merge = true를 사용하여 기존 사용자의 다른 필드를 유지하면서 업데이트
            userRef.set(data, encodeDefaults = true, merge = true)
            Napier.d("사용자 연결 정보 저장 성공: $userId")
        } catch (e: Exception) {
            Napier.e("사용자 연결 정보 저장 실패: ${e.message}")
        }
    }

    override suspend fun getFcmToken(): String? {
        return try {
            messaging.getToken()
        } catch (e: Exception) {
            Napier.e("FCM 토큰 가져오기 실패: ${e.message}")
            null
        }
    }
}
