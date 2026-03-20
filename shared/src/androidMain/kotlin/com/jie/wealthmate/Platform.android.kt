package com.jie.wealthmate

import android.os.Build
import kotlin.system.exitProcess

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override fun exitApp() {
        exitProcess(0)
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()