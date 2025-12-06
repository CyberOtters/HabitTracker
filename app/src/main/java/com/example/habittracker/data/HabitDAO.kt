package com.example.habittracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HabitDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit)

    // TODO: Delete if not necessary
    @Query("SELECT * FROM ${AppDatabase.HABIT_TABLE_NAME} ORDER BY createdAt DESC")
    suspend fun getAllHabit(): List<Habit>

    @Query("SELECT * FROM ${AppDatabase.HABIT_TABLE_NAME} WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getHabitByUserId(userId: Int): List<Habit>

}