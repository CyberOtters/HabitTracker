package com.example.habittracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitCategory
import com.example.habittracker.data.HabitCategoryDao
import com.example.habittracker.data.HabitDAO
import com.example.habittracker.data.User
import com.example.habittracker.data.UserDao
import com.example.habittracker.utils.hashPassword
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date


@RunWith(AndroidJUnit4::class)
class HabitCategoryTest {

    private lateinit var userDao: UserDao
    private lateinit var habitCategoryDao: HabitCategoryDao

    private lateinit var habitDao: HabitDAO
    private lateinit var db: AppDatabase

    private lateinit var testUser: User

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        userDao = db.userDao()
        habitDao = db.habitDao()
        habitCategoryDao = db.habitCategoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Before
    fun setup() = runTest {
        // Create testUser
        val (salt, hash) = hashPassword("password123")
        testUser = User(
            username = "bob",
            isAdmin = false,
            passwordSalt = salt,
            passwordHash = hash,
            createdAt = Date(),
            updatedAt = Date()
        )

        userDao.insert(user = testUser)

        val byUsername = userDao.getByUsername(testUser.username)
        Assert.assertEquals(testUser.username, byUsername?.username)
        Assert.assertEquals(testUser.passwordHash, byUsername?.passwordHash)
        Assert.assertEquals(testUser.passwordSalt, byUsername?.passwordSalt)
        Assert.assertEquals(testUser.isAdmin, byUsername?.isAdmin)

        // Add habits for testUser
        testUser = byUsername!!
        habitDao.insert(
            habit = Habit(
                userId = testUser.userId,
                name = "Test Habit 1",
                description = "This is a the description for Test Habit 1",
                createdAt = Date(),
                updatedAt = Date()
            )
        )
        habitDao.insert(
            habit = Habit(
                userId = testUser.userId,
                name = "Test Habit 2",
                description = "This is a the description for Test Habit 2",
                createdAt = Date(),
                updatedAt = Date()
            )
        )
        habitDao.insert(
            habit = Habit(
                userId = testUser.userId,
                name = "Test Habit 3",
                description = "This is a the description for Test Habit 3",
                createdAt = Date(),
                updatedAt = Date()
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun insertHabitCategoryTest() = runTest {
        val categories = listOf("Health", "Productivity", "Wellness")

        for (categoryName in categories) {
            val categoryId = habitCategoryDao.insert(
                habitCategory = HabitCategory(
                    name = categoryName,
                    userId = testUser.userId,
                    description = "Category for $categoryName related habits",
                    createdAt = Date(),
                    updatedAt = Date()
                )
            )
            val retrievedCategory = habitCategoryDao.getById(categoryId.toInt())
            Assert.assertNotNull(retrievedCategory)
            Assert.assertEquals(categoryName, retrievedCategory?.name)
        }
    }

    @Test
    @Throws(Exception::class)
    fun updateHabitCategoryTest() = runTest {
        val categoryId = habitCategoryDao.insert(
            habitCategory = HabitCategory(
                name = "Fitness",
                userId = testUser.userId,
                description = "Category for fitness related habits",
                createdAt = Date(),
                updatedAt = Date()
            )
        )
        val retrievedCategory = habitCategoryDao.getById(categoryId.toInt())
        Assert.assertNotNull(retrievedCategory)
        Assert.assertEquals("Fitness", retrievedCategory?.name)

        // Update category name
        val updatedCategory = retrievedCategory!!.copy(
            name = "Health & Fitness",
            updatedAt = Date()
        )
        habitCategoryDao.update(updatedCategory)
        val afterUpdateCategory = habitCategoryDao.getById(categoryId.toInt())
        Assert.assertNotNull(afterUpdateCategory)
        Assert.assertEquals("Health & Fitness", afterUpdateCategory?.name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteHabitCategoryTest() = runTest {
        val categoryId = habitCategoryDao.insert(
            habitCategory = HabitCategory(
                name = "test category",
                userId = testUser.userId,
                description = "test description",
                createdAt = Date(),
                updatedAt = Date()
            )
        )
        val retrievedCategory = habitCategoryDao.getById(categoryId.toInt())
        Assert.assertNotNull(retrievedCategory)

        habitCategoryDao.delete(retrievedCategory!!)

        val afterDeleteCategory = habitCategoryDao.getById(retrievedCategory.habitCategoryId)
        Assert.assertNull(afterDeleteCategory)
    }
}