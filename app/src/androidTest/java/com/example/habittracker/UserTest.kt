package com.example.habittracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.User
import com.example.habittracker.data.UserDao
import com.example.habittracker.utils.hashPassword
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date
import kotlinx.coroutines.test.runTest


@RunWith(AndroidJUnit4::class)
class UserTest {
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userDao = db.userDao()
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
}