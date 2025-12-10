package com.example.habittracker.data

import android.content.Context
import com.example.habittracker.MainActivity.Companion.USER_ID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepository @Inject constructor(
    context: Context
) {
    val db = AppDatabase.getDatabase(context)
    val sharedPrefs = context.getSharedPreferences("HabitTrackerPrefs", Context.MODE_PRIVATE)

    /*******************
     * User Operations *
     *******************/

    suspend fun deleteUser(user: User) {
        if (user.userId == sharedPrefs.getInt(USER_ID, -1)) {
            throw IllegalArgumentException("Cannot delete the currently logged-in user.")
        }
        db.userDao().delete(user)
    }

    suspend fun getUserById(userId: Int) = db.userDao().getById(userId)

    suspend fun getUserByUsername(username: String) = db.userDao().getByUsername(username)

    fun getAllUsers() = db.userDao().getAllUsers()

    suspend fun getAllHabit() = db.habitDao().getAllHabit()

    /********************
     * Habit Operations *
     ********************/
    suspend fun getHabitByUserId(userId: Int) = db.habitDao().getHabitByUserId(userId)

    /***********************
     * HabitLog Operations *
     ***********************/
    suspend fun getHabitLogsForUser(userId: Int) =
        db.habitLogDao().getHabitLogsForUser(userId)

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