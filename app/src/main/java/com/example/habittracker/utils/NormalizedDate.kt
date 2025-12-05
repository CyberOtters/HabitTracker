package com.example.habittracker.utils

import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * A value class that represents a date normalized to UTC midnight.
 * This is useful for storing dates without time components in a database.
 */
@JvmInline
value class NormalizedDate(val utcMidnightMillis: Long) {

    companion object {

        fun from(date: Date): NormalizedDate {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.time = date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return NormalizedDate(cal.timeInMillis)
        }

        fun now(): NormalizedDate = from(Date())
    }

    fun toDate(): Date = Date(utcMidnightMillis)
}
