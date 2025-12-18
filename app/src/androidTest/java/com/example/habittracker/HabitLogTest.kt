package com.example.habittracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habittracker.data.*
import com.example.habittracker.utils.NormalizedDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Calendar
import java.util.Date

@RunWith(AndroidJUnit4::class)
class HabitLogTest {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var habitDAO: HabitDAO
    private lateinit var habitLogDao: HabitLogDao


    private lateinit var testUser: User
    private lateinit var testHabit: Habit

    @Before
    fun createDb() = runTest {

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        userDao = db.userDao()
        habitDAO = db.habitDao()
        habitLogDao = db.habitLogDao()

        val user = User(
            username = "log_tester",
            isAdmin = false,
            passwordSalt = "",
            passwordHash = "",
            createdAt = Date(),
            updatedAt = Date()
        )

        val userId = userDao.insert(user).toInt()
        testUser = user.copy(userId = userId)

        val habit = Habit(

            userId = userId,
            name = "Morning Jog",
            points = 5,
            description = "Run 5km",
            createdAt = Date(),
            updatedAt = Date()
        )
        val habitId = habitDAO.insert(habit).toInt()
        testHabit = habit.copy(habitId = habitId)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveHabitLogTest() = runTest {
        val today = NormalizedDate.from(Date())

        val newLog = HabitLog(
            habitId = testHabit.habitId,
            userId = testUser.userId,
            date = today,
            note = "Felt great!",
            completed = false,
            createdAt = Date(),
            updatedAt = Date()
        )

        val logId = habitLogDao.insertHabitLog(newLog).toInt()
        val retrievedLog = habitLogDao.getHabitLogById(logId)

        Assert.assertNotNull(retrievedLog)
        Assert.assertEquals(newLog.note, retrievedLog?.note)
        Assert.assertEquals(newLog.habitId, retrievedLog?.habitId)
    }

    @Test
    fun updateHabitLogStatusTest() = runTest {
        val today = NormalizedDate.from(Date())
        val log = HabitLog(
            habitId = testHabit.habitId,
            userId = testUser.userId,
            date = today,
            completed = false,
            createdAt = Date(),
            updatedAt = Date()

        )

        val logId = habitLogDao.insertHabitLog(log).toInt()

        val originalLog = habitLogDao.getHabitLogById(logId)
        Assert.assertNotNull(originalLog)

        val updatedLog = originalLog!!.copy(
            completed = true,
            note = "Finished successfully",
            updatedAt = Date()
        )
        habitLogDao.updateHabitLog(updatedLog)

        val finalLog = habitLogDao.getHabitLogById(logId)
        Assert.assertEquals(true, finalLog?.completed)
        Assert.assertEquals("Finished successfully", finalLog?.note)
    }

    @Test
    fun getHabitLogsForDateRangeTest() = runTest {
        val cal = Calendar.getInstance()

        val todayDate = NormalizedDate.from(cal.time)

        val logToday = HabitLog(

            habitId = testHabit.habitId,
            userId = testUser.userId,
            date = todayDate,
            createdAt = Date(),
            updatedAt = Date()
        )
        habitLogDao.insertHabitLog(logToday)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayDate = NormalizedDate.from(cal.time)
        val logYesterday = HabitLog(
            habitId = testHabit.habitId,
            userId = testUser.userId,
            date = yesterdayDate,
            createdAt = Date(),
            updatedAt = Date()
        )
        habitLogDao.insertHabitLog(logYesterday)

        cal.add(Calendar.MONTH, -1)
        val lastMonthDate = NormalizedDate.from(cal.time)
        val logLastMonth = HabitLog(
            habitId = testHabit.habitId,
            userId = testUser.userId,
            date = lastMonthDate,
            createdAt = Date(),
            updatedAt = Date()
        )
        habitLogDao.insertHabitLog(logLastMonth)

        val retrievedLogs = habitLogDao.getHabitLogsForDateRange(
            userId = testUser.userId,
            startDate = yesterdayDate,
            endDate = todayDate
        ).first()


        Assert.assertEquals(2, retrievedLogs.size)
        Assert.assertTrue(retrievedLogs.any { it.date == todayDate })

        Assert.assertTrue(retrievedLogs.any { it.date == yesterdayDate })
    }
}