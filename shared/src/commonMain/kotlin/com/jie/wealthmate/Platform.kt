package com.jie.wealthmate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform