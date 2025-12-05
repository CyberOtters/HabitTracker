package com.example.habittracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = AppDatabase.USER_TABLE_NAME,
    indices = [
        Index(value = ["username"], unique = true)
    ]
)

data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val username: String,

    @ColumnInfo(defaultValue = "0" )
    val isAdmin: Boolean,

    val passwordHash: String,

    val passwordSalt: String,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)" )
    val createdAt: Date,

    @ColumnInfo(defaultValue = "(strftime('%s','now') * 1000)" )
    val updatedAt: Date
)