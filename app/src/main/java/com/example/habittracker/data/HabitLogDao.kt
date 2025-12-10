package com.example.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.habittracker.utils.NormalizedDate
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(habitLog: HabitLog): Long

    @Update
    suspend fun updateHabitLog(habitLog: HabitLog)

    @Delete
    suspend fun deleteHabitLog(habitLog: HabitLog)
    //Removes a log entry

    @Query("SELECT * FROM HabitLog WHERE habitLogId = :id")
    suspend fun getHabitLogById(id: Int): HabitLog?

    @Query("SELECT * FROM HabitLog WHERE userId = :userId AND habitId = :habitId AND date = :date")
    suspend fun getHabitLogForDate(
        userId: Int,
        habitId: Int,
        date: NormalizedDate
    ): HabitLog?

    @Query("SELECT * FROM HabitLog WHERE userId = :userId ORDER BY date DESC")
    fun getHabitLogsForUser(userId: Int): Flow<List<HabitLog>>
    //Gets all the logs for a user starting with the newest date
}
