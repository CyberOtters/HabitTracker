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
    tableName = AppDatabase.HABIT_CATEGORY_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"])
    ]
)
data class HabitCategory(
    @PrimaryKey(autoGenerate = true)
    val habitCategoryId: Int = 0,

    val userId: Int,

    val name: String,

    val description: String?,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)" )
    val createdAt: Date,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)" )
    val updatedAt: Date
)




@Entity(
    tableName = AppDatabase.HABIT_CATEGORY_CROSS_REF_TABLE_NAME,
    primaryKeys = ["habitId", "habitCategoryId"],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["habitId"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HabitCategory::class,
            parentColumns = ["habitCategoryId"],
            childColumns = ["habitCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["habitCategoryId"]),
    ]
)
data class HabitCategoryCrossRef(
    val habitId: Int,
    val habitCategoryId: Int
)

data class HabitCategoryWithHabits(
    @Embedded val habitCategory: HabitCategory,
    @Relation(
        parentColumn = "habitCategoryId",
        entityColumn = "habitId",
        associateBy = Junction(HabitCategoryCrossRef::class)
    )
    val habits: List<Habit>
)