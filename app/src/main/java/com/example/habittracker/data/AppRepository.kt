package com.example.habittracker.data

import android.content.Context
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepository @Inject constructor(
    context: Context
) {
    val db = AppDatabase.getDatabase(context)

    suspend fun getUserById(userId: Int) = db.userDao().getById(userId)

    suspend fun getUserByUsername(username: String) = db.userDao().getByUsername(username)

    // TODO: Delete if not necessary
    suspend fun getAllHabit() = db.habitDao().getAllHabit()

    suspend fun getHabitByUserId(userId: Int) = db.habitDao().getHabitByUserId(userId)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppRepository? = null


        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: AppRepository(context).also { instance = it }
            }
    }
}