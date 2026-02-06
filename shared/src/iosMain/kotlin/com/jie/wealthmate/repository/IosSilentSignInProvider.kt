package com.jie.wealthmate.repository

import com.jie.wealthmate.entity.GoogleAuthEntity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * iOS App(Swift)에서 실제 GIDSignIn 로직을 구현하여 주입해주기 위한 객체입니다.
 * Swift와의 호환성을 위해 콜백 방식을 사용합니다.
 */
object IosSilentSignInProvider {
    // Swift에서 구현할 콜백 타입 정의
    // (성공 시 GoogleAuthEntity 반환, 실패 시 null 반환)
    private var provider: ((onComplete: (GoogleAuthEntity?) -> Unit) -> Unit)? = null

    fun setProvider(callback: (onComplete: (GoogleAuthEntity?) -> Unit) -> Unit) {
        this.provider = callback
    }

    suspend fun performSilentSignIn(): GoogleAuthEntity? {
        val currentProvider = provider ?: return null
        
        return suspendCancellableCoroutine { continuation ->
            currentProvider { entity ->
                continuation.resume(entity)
            }
        }
    }
}
