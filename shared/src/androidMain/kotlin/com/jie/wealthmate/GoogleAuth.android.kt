package com.jie.wealthmate

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.aakira.napier.Napier

// android
actual fun createSettings(context: Any?): Settings {

    val appContext = context as? Context ?: throw IllegalArgumentException("Context required")

    return try {
        val masterKey = MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            appContext,
            "secure_auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        SharedPreferencesSettings(sharedPreferences)
    } catch (e: Exception) {
        Napier.e("Failed to create EncryptedSharedPreferences, falling back to regular SharedPreferences", e)
        // Fallback to regular SharedPreferences if encryption fails (e.g. key store issues)
        val sharedPreferences = appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(sharedPreferences)
    }
}
