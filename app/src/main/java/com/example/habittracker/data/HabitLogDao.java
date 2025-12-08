package com.example.habittracker.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
interface HabitLogDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabitLog(habitLog: HabitLog): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabitLogs(habitLogs: List<HabitLog>): List<Long>

  @Update
  suspend fun updateHabitLog(habitLog: HabitLog)

  @Delete
  suspend fun deleteHabitLog(habitLog: HabitLog)

  @Query("SELECT * FROM habit_logs WHERE id = :id LIMIT 1")
  suspend fun getHabitLogById(id: Int): HabitLog?

  @Query("SELECT * FROM habit_logs WHERE userId = :userId ORDER BY date DESC")
  suspend fun getHabitLogsForUser(userId: Int): List<HabitLog>
}
