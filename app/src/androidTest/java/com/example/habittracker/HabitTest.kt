package com.example.habittracker

import android.content.Context
import androidx.compose.runtime.snapshots.toInt
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.HabitDAO
import com.example.habittracker.data.User
import com.example.habittracker.data.UserDao
import com.example.habittracker.utils.hashPassword
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.Date
import com.example.habittracker.data.Habit

class HabitTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var habitDao: HabitDAO

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userDao = db.userDao()
        habitDao = db.habitDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertUserTest() = runTest {
        val (salt, hash) = hashPassword("password123")
        val testUser = User(
            username = "bob",
            isAdmin = false,
            passwordSalt = salt,
            passwordHash = hash,
            createdAt = Date(),
            updatedAt = Date()
        )

        userDao.insert(user = testUser)

        val byName = userDao.getByUsername(testUser.username)
        Assert.assertEquals(testUser.username, byName?.username)
        Assert.assertEquals(testUser.passwordHash, byName?.passwordHash)
        Assert.assertEquals(testUser.passwordSalt, byName?.passwordSalt)
        Assert.assertEquals(testUser.isAdmin, byName?.isAdmin)
    }

    @Test
    @Throws(Exception::class)
    fun addHabitTest() = runTest {
        val (salt, hash) = hashPassword("password123")
        val testUser = User(
            username = "bob",
            isAdmin = false,
            passwordSalt = salt,
            passwordHash = hash,
            createdAt = Date(),
            updatedAt = Date()
        )
        userDao.insert(user = testUser)

        val insertedUser = userDao.getByUsername("bob")!!
        val habit = Habit(
            userId = insertedUser.userId,
            name = "Add habit",
            description = "This is to test add habit",
            createdAt = Date(),
            updatedAt = Date()
        )

        val habitId = habitDao.insert(habit)

        val retrievedHabit = habitDao.getHabitById(habitId.toInt())
        Assert.assertNotNull(retrievedHabit)
        Assert.assertEquals("Add habit", retrievedHabit?.name)
        Assert.assertEquals(insertedUser.userId, retrievedHabit?.userId)
        Assert.assertEquals("This is to test add habit", retrievedHabit?.description)

        val habitTwo = Habit(
            userId = insertedUser.userId,
            name = "Add Second habit",
            description = "This is to test add habit and see if user is able to add multiple habits",
            createdAt = Date(),
            updatedAt = Date()
        )

        val habitIdTwo = habitDao.insert(habitTwo)
        val retrievedHabitTwo = habitDao.getHabitById(habitIdTwo.toInt())
        Assert.assertNotNull(retrievedHabitTwo)
        Assert.assertEquals("Add Second habit", retrievedHabitTwo?.name)
        Assert.assertEquals(insertedUser.userId, retrievedHabitTwo?.userId)
        Assert.assertEquals("This is to test add habit and see if user is able to add multiple habits", retrievedHabitTwo?.description)

    }

    @Test
    @Throws(Exception::class)
    fun deleteHabitTest() = runTest {
        val (salt, hash) = hashPassword("password123")
        val testUser = User(
            username = "bob",
            isAdmin = false,
            passwordSalt = salt,
            passwordHash = hash,
            createdAt = Date(),
            updatedAt = Date()
        )
        userDao.insert(user = testUser)
        val insertedUser = userDao.getByUsername("bob")!!
        val habit = Habit(
            userId = insertedUser.userId,
            name = "Add habit",
            description = "This is to test add habit",
            createdAt = Date(),
            updatedAt = Date()
        )

        val habitId = habitDao.insert(habit)

        val retrievedHabit = habitDao.getHabitById(habitId.toInt())
        Assert.assertNotNull(retrievedHabit)

        // Delete the habit
        habitDao.delete(retrievedHabit!!)
        val deletedHabit = habitDao.getHabitById(habitId.toInt())
        Assert.assertNull(deletedHabit)
    }

    @Test
    @Throws(Exception::class)
    fun editHabitTest() = runTest {
        val (salt, hash) = hashPassword("password123")
        val testUser = User(
            username = "bob",
            isAdmin = false,
            passwordSalt = salt,
            passwordHash = hash,
            createdAt = Date(),
            updatedAt = Date()
        )
        userDao.insert(user = testUser)

        val insertedUser = userDao.getByUsername("bob")!!
        val habit = Habit(
            userId = insertedUser.userId,
            name = "Add habit",
            description = "This is to test add habit",
            createdAt = Date(),
            updatedAt = Date()
        )

        val habitId = habitDao.insert(habit)

        val originalHabit = habitDao.getHabitById(habitId.toInt())
        Assert.assertEquals("Add habit", originalHabit?.name)


        val editHabit = Habit(
            habitId = habitId.toInt(),
            userId = insertedUser.userId,
            name = "Edit habit",
            description = "This is to test editing habit if it works",
            createdAt = Date(),
            updatedAt = Date()
        )

        //Perform the update
        habitDao.update(editHabit)

        val updatedHabit = habitDao.getHabitById(habitId.toInt())

        Assert.assertNotNull(updatedHabit)
        Assert.assertNotEquals("Add habit", updatedHabit?.name)
        Assert.assertEquals("Edit habit", updatedHabit?.name)
        Assert.assertNotEquals("This is to test add habit", updatedHabit?.description)
        Assert.assertEquals("This is to test editing habit if it works", updatedHabit?.description)
    }
}
