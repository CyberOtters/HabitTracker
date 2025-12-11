package com.example.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM ${AppDatabase.USER_TABLE_NAME} WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?

    @Query("SELECT * FROM ${AppDatabase.USER_TABLE_NAME}")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM ${AppDatabase.USER_TABLE_NAME} WHERE userId = :id LIMIT 1")
    suspend fun getById(id: Int): User?

    @Insert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)
}