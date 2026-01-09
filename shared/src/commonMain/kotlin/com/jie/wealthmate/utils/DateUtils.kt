package com.jie.wealthmate.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock


const val formatDateHyphen: String = "yyyy-MM-dd"

const val formatDateKor: String = "yyyy년 M월 d일"
const val formatDateKorYM: String = "yyyy년 M월"


val nowLocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * only hyphen
 */
fun String?.convertDate(
    convertPattern: String,
    defaultValue: String = "",
): String {
    try {
        if (this.isNullOrEmpty().not()) {
            val parts = this.split("-")
            if (parts.isNotEmpty()) {
                val year = parts[0].toInt()
                val month = parts.getOrNull(1)?.toIntOrNull() ?: 1
                val day = parts.getOrNull(2)?.toIntOrNull() ?: 1
                val localDate = LocalDate(year, month, day)
                return when (convertPattern) {
                    formatDateKorYM -> "${localDate.year}년 ${localDate.month.number}월"
                    formatDateKor -> "${localDate.year}년 ${localDate.month.number}월 ${localDate.day}일"
                    else -> defaultValue
                }
            }
        }
    } catch (e: Exception) {
        Unit
    }
    return defaultValue
}

/**
 * only hyphen
 */
fun String?.convertDateToLocalDate(): LocalDate? {
    try {
        if (this.isNullOrEmpty().not()) {
            val parts = this.split("-")
            if (parts.size >= 3) {
                val year = parts[0].toInt()
                val month = parts[1].toInt()
                val day = parts[2].toInt()
                return LocalDate(year, month, day)
            }
        }
    } catch (e: Exception) {
        Unit
    }
    return null
}