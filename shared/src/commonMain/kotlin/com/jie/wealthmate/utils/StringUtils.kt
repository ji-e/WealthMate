package com.jie.wealthmate.utils

import kotlin.jvm.JvmName

fun formatWithCommas(number: String): String {
    if (number.isEmpty()) return ""

    // 음수 부호 확인
    val isNegative = number.startsWith("-")
    val numberWithoutSign = if (isNegative) number.substring(1) else number

    if (numberWithoutSign.isEmpty()) return ""

    // 콤마 추가
    val reversed = numberWithoutSign.reversed()
    val withCommas = reversed.chunked(3).joinToString(",")
    val formatted = withCommas.reversed()

    // 음수면 부호 다시 붙이기
    return if (isNegative) "-$formatted" else formatted
}

fun Long.formatWithCommas(): String {
    return formatWithCommas(this.toString())
}

@JvmName("formatWithCommasExt")
fun String.formatWithCommas(): String {
    if (this.isEmpty()) return ""

    // 음수 부호 확인
    val isNegative = this.startsWith("-")
    val numberWithoutSign = if (isNegative) this.substring(1) else this

    if (numberWithoutSign.isEmpty()) return ""

    // 콤마 추가
    val reversed = numberWithoutSign.reversed()
    val withCommas = reversed.chunked(3).joinToString(",")
    val formatted = withCommas.reversed()

    // 음수면 부호 다시 붙이기
    return if (isNegative) "-$formatted" else formatted
}

fun String.formatRemoveCommas(): String {
    return replace(",", "")
}
