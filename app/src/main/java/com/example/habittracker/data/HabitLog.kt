package com.example.habittracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.habittracker.utils.NormalizedDate
import java.util.Date

@Entity(
    tableName = AppDatabase.HABIT_LOG_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["habitId"]),
        // Unique constraint to ensure one log entry per habit per user per day
        Index(value = ["habitId", "date", "userId"], unique = true),
    ]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val habitId: Int,

    val userId: Int,

    val date: NormalizedDate,

    val note: String? = null,

    val completed: Boolean? = null,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)")
    val updatedAt: Date
)
