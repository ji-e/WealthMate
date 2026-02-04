package com.jie.wealthmate

import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings

// iOS
actual fun createSettings(context: Any?): Settings {
    return KeychainSettings(service = "com.jie.wealthmate.WealthMate")
}