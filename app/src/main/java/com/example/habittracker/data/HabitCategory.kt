package com.example.habittracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = AppDatabase.HABIT_CATEGORY_TABLE_NAME,
)
data class HabitCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val description: String?,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)" )
    val createdAt: Date,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)" )
    val updatedAt: Date
)




@Entity(
    tableName = AppDatabase.HABIT_CATEGORY_CROSS_REF_TABLE_NAME,
    primaryKeys = ["habitId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HabitCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["categoryId"]),
    ]
)
data class HabitCategoryCrossRef(
    val habitId: Int,
    val categoryId: Int
)

