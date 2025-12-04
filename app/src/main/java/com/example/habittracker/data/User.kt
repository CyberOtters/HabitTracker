package com.example.habittracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = AppDatabase.USER_TABLE_NAME,
    indices = [
        Index(value = ["username"], unique = true)
    ]
)

data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val username: String,

    val passwordHash: String,

    val passwordSalt: String,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)