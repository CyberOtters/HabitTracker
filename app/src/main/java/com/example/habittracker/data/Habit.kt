package com.example.habittracker.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(
    tableName = AppDatabase.HABIT_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
    ]
)

data class Habit(
    @PrimaryKey(autoGenerate = true)
    val habitId: Int = 0,

    val userId: Int,

    val name: String,

    val description: String? = null,

    val points: Int = 0,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)")
    val updatedAt: Date = Date(),
) {
    init {
        require(points in 0..5) { "points must be between -5 and 5" }
    }
}

data class HabitWithHabitCategories(
    @Embedded val habit: Habit,
    @Relation(
        parentColumn = "habitId",
        entityColumn = "categoryId",
        associateBy = Junction(HabitCategoryCrossRef::class)
    )
    val songs: List<HabitCategory>
)