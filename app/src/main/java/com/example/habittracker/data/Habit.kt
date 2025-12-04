package com.example.habittracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = AppDatabase.HABIT_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
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
    val id: Int = 0,

    val userId: Int,

    val name: String,

    val description: String? = null,

    val points : Int = 0,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP" )
    val createdAt: Long,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP" )
    val updatedAt: Long
){
    init {
        require(points in -5..5) { "points must be between -5 and 5" }
    }
}
