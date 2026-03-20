package com.jie.wealthmate

interface Platform {
    val name: String
    fun exitApp()
}

expect fun getPlatform(): Platform