package com.jie.wealthmate.repository

import com.jie.wealthmate.entity.GoogleAuthEntity
import io.github.aakira.napier.Napier

actual suspend fun platformSilentSignIn(): GoogleAuthEntity? {
    Napier.d("iOS: Requesting silent sign-in from Native Provider...")
    // Swift에서 주입해준 로직을 호출합니다.
    return IosSilentSignInProvider.performSilentSignIn()
}
