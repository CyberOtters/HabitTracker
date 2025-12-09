package com.example.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

import androidx.room.Query
import androidx.room.Transaction

@Dao
interface HabitCategoryDao {
    @Transaction
    @Query("SELECT * FROM ${AppDatabase.HABIT_CATEGORY_TABLE_NAME}")
    suspend fun getAll(): List<HabitCategoryWithHabits>

    @Insert
    suspend fun insert(habitCategory: HabitCategory): Long

    @Query("SELECT * FROM ${AppDatabase.HABIT_CATEGORY_TABLE_NAME} WHERE habitCategoryId = :habitCategoryId LIMIT 1")
    suspend fun getById(habitCategoryId: Int): HabitCategory?
}