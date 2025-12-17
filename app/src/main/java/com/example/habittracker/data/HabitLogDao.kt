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

    @Query("SELECT * FROM HabitLog WHERE userId = :userId AND habitId = :habitId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getHabitLogsForHabit(
        userId: Int,
        habitId: Int,
        startDate: NormalizedDate,
        endDate: NormalizedDate
    ): Flow<List<HabitLog>>

    @Query("""
    SELECT 
        hl.habitLogId AS habitLogId,
        hl.date AS date,
        h.name AS activity,
        hl.completed AS completed,
        hl.note AS note
    FROM HabitLog AS hl
    INNER JOIN Habit AS h
        ON h.habitId = hl.habitId
    WHERE hl.userId = :userId
    ORDER BY hl.date DESC, h.name ASC
""")
    fun getHabitLogRowsForUser(userId: Int): Flow<List<HabitLogRow>>


    @Query("""
        SELECT 
            hl.habitLogId AS habitLogId,
            hl.date AS date,
            h.name AS activity,
            hl.completed AS completed,
            hl.note AS note
        FROM HabitLog AS hl
        INNER JOIN Habit AS h
            ON h.habitId = hl.habitId
        WHERE hl.userId = :userId AND hl.habitId = :habitId
    ORDER BY hl.updatedAt DESC, hl.habitLogId DESC    
    """)
    fun getHabitLogRowsForHabit(userId: Int, habitId: Int): Flow<List<HabitLogRow>>
}
