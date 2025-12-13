package com.example.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(habit: Habit)

    @Delete
    fun delete(habit: Habit)

    @Update
    suspend fun update(habit: Habit)


    @Query("SELECT * FROM ${AppDatabase.HABIT_TABLE_NAME} ORDER BY createdAt DESC")
    suspend fun getAllHabits(): List<Habit>

    @Query("SELECT * FROM ${AppDatabase.HABIT_TABLE_NAME} WHERE habitId = :habitId")
    fun getHabitById(habitId: Int): Flow<Habit>


    @Query("SELECT * FROM ${AppDatabase.HABIT_TABLE_NAME} WHERE userId = :userId ORDER BY createdAt DESC")
    fun getHabitsByUserId(userId: Int): Flow<List<Habit>>
}