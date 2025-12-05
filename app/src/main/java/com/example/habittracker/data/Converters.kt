package com.example.habittracker.data

import androidx.room.TypeConverter
import com.example.habittracker.utils.NormalizedDate
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toMillis(nd: NormalizedDate?): Long? =
        nd?.utcMidnightMillis

    @TypeConverter
    fun fromMillis(value: Long?): NormalizedDate? =
        value?.let { NormalizedDate(it) }
}
