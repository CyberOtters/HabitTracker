package com.example.habittracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.habittracker.data.AppDatabase
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
class UserTest {
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
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

        val byUsername = userDao.getByUsername(testUser.username)
        Assert.assertEquals(testUser.username, byUsername?.username)
        Assert.assertEquals(testUser.passwordHash, byUsername?.passwordHash)
        Assert.assertEquals(testUser.passwordSalt, byUsername?.passwordSalt)
        Assert.assertEquals(testUser.isAdmin, byUsername?.isAdmin)
    }

    @Test
    @Throws(Exception::class)
    fun deleteUserTest() = runTest {
        val (salt, hash) = hashPassword("password123")
        val testUser = User(
            username = "alice",
            isAdmin = true,
            passwordSalt = salt,
            passwordHash = hash,
            createdAt = Date(),
            updatedAt = Date()
        )

        userDao.insert(user = testUser)
        var byUsername = userDao.getByUsername(testUser.username)
        Assert.assertNotNull(byUsername)

        userDao.delete(byUsername!!)

        userDao.delete(user = testUser)
        byUsername = userDao.getByUsername(testUser.username)
        Assert.assertNull(byUsername)
    }

    @Test
    @Throws(Exception::class)
    fun updatePasswordTest() = runTest {
        val (salt, hash) = hashPassword("password123")
        val testUser = User(
            username = "charlie",
            isAdmin = false,
            passwordSalt = salt,
            passwordHash = hash,
            createdAt = Date(),
            updatedAt = Date()
        )

        userDao.insert(user = testUser)

        var byUsername = userDao.getByUsername(testUser.username)
        Assert.assertEquals(testUser.passwordHash, byUsername?.passwordHash)
        // Update password
        val (newSalt, newHash) = hashPassword("newpassword456")
        val updatedUser = byUsername!!.copy(
            passwordSalt = newSalt,
            passwordHash = newHash,
            updatedAt = Date()
        )
        userDao.update(updatedUser)
        byUsername = userDao.getByUsername(testUser.username)
        Assert.assertEquals(newHash, byUsername?.passwordHash)
        Assert.assertEquals(newSalt, byUsername?.passwordSalt)
    }
}