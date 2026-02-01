package com.jie.wealthmate.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


const val formatDateHyphen: String = "yyyy-MM-dd"
const val formatDateHyphenYMDE: String = "yyyy-MM-dd (E)"

const val formatDateKor: String = "yyyy년 M월 d일"
const val formatDateKorYM: String = "yyyy년 M월"
const val formatDateKorMD: String = "M월 d일"

const val formatDateDotYYMD: String = "yy.M.d"


@OptIn(ExperimentalTime::class)
val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
@OptIn(ExperimentalTime::class)
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

                return localDate.convertLocalDateToString(convertPattern, defaultValue)
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

/**
 * LocalDate -> String
 */
fun LocalDate?.convertLocalDateToString(
    convertPattern: String,
    defaultValue: String = "",
): String {

    this ?: return defaultValue

    return when (convertPattern) {
        formatDateHyphenYMDE -> this.toString() + " (${WeekEnum.creator(this.dayOfWeek.isoDayNumber).korDisplayName})"
        formatDateKor -> "${this.year}년 ${this.month.number}월 ${this.day}일"
        formatDateKorYM -> "${this.year}년 ${this.month.number}월"
        formatDateKorMD -> "${this.month.number}월 ${this.day}일 "
        formatDateDotYYMD -> "${this.year.toString().takeLast(2)}.${this.month.number}.${this.day}"
        else -> defaultValue
    }
}

/**
 * 월의 마지막 날짜
 */
fun LocalDate.lastDayOfMonth(): LocalDate {
    val nextMonth = this.plus(1, DateTimeUnit.MONTH)
    val firstDayOfNextMonth = LocalDate(nextMonth.year, nextMonth.month, 1)
    return firstDayOfNextMonth.minus(1, DateTimeUnit.DAY)
}

/**
 * 월의 첫번째 날짜
 */
fun LocalDate.firstDayOfMonth(): LocalDate {
    return LocalDate(this.year, this.month, 1)
}

/**
 * toEpochMilliseconds
 */
fun LocalDate.toEpochMilliseconds(): Long {
    return this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

/**
 * EpochMilliseconds를 LocalDate로 변환
 */
fun Long?.toLocalDate(): LocalDate {
    this ?: return today
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
}


private enum class WeekEnum(val korDisplayName: String, val isoDayNumber: Int) {
    SUN(
        korDisplayName = "일",
        isoDayNumber = 7
    ),
    MON(
        korDisplayName = "월",
        isoDayNumber = 1
    ),
    TUE(
        korDisplayName = "화",
        isoDayNumber = 2
    ),
    WED(
        korDisplayName = "수",
        isoDayNumber = 3
    ),
    THU(
        korDisplayName = "목",
        isoDayNumber = 4
    ),
    FRI(
        korDisplayName = "금",
        isoDayNumber = 5
    ),
    SAT(
        korDisplayName = "토",
        isoDayNumber = 6
    );

    companion object {
        fun creator(isoDayNumber: Int): WeekEnum =
            WeekEnum.entries.find { it.isoDayNumber == isoDayNumber } ?: MON
    }

}
