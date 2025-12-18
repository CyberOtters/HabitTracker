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
class HabitReviewTest {
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
            username = "stats_user",
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
            name = "Meditation",
            points = 5,
            description = "Daily mindfulness",
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
    fun calculateWeeklyStatsTest() = runTest {
        val cal = Calendar.getInstance()
        val today = NormalizedDate.from(cal.time)

        habitLogDao.insertHabitLog(HabitLog(
            habitId = testHabit.habitId, userId = testUser.userId, date = today, completed = true,
            createdAt = Date(), updatedAt = Date()
        ))

        cal.add(Calendar.DAY_OF_YEAR, -1)
        habitLogDao.insertHabitLog(HabitLog(
            habitId = testHabit.habitId, userId = testUser.userId, date = NormalizedDate.from(cal.time), completed = true,
            createdAt = Date(), updatedAt = Date()
        ))

        cal.add(Calendar.DAY_OF_YEAR, -1)
        habitLogDao.insertHabitLog(HabitLog(
            habitId = testHabit.habitId, userId = testUser.userId, date = NormalizedDate.from(cal.time), completed = false,
            createdAt = Date(), updatedAt = Date()
        ))

        val endDate = today
        cal.add(Calendar.DAY_OF_YEAR, -5)
        val startDate = NormalizedDate.from(cal.time)

        val logs = habitLogDao.getHabitLogsForDateRange(testUser.userId, startDate, endDate).first()

        val totalLogged = logs.size
        val completedCount = logs.count { it.completed == true }
        val totalPoints = completedCount * testHabit.points

        Assert.assertEquals(3, totalLogged)
        Assert.assertEquals(2, completedCount)
        Assert.assertEquals(10, totalPoints)
    }
}