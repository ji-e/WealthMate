package com.jie.wealthmate.utils

fun formatWithCommas(number: String): String {
    if (number.isEmpty()) return ""

    val reversed = number.reversed()
    val withCommas = reversed.chunked(3).joinToString(",")
    return withCommas.reversed()
}

fun String.formatRemoveCommas(): String {
    return replace(",", "")
}
