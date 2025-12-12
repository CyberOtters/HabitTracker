package com.example.habittracker.data

import android.content.Context
import com.example.habittracker.MainActivity.Companion.SHARED_PREFS_NAME
import com.example.habittracker.MainActivity.Companion.USER_ID
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepository @Inject constructor(
    context: Context
) {
    val db = AppDatabase.getDatabase(context)
    val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    /*******************
     * User Operations *
     *******************/

    suspend fun deleteUser(user: User) {
        val loggedInUserId = sharedPrefs.getInt(USER_ID, -1)
        if (user.userId == loggedInUserId) {
            throw IllegalArgumentException("Cannot delete the currently logged-in user.")
        }
        val loggedInUser = getUserById(loggedInUserId)
        if (loggedInUser?.isAdmin != true) {
            throw IllegalArgumentException("Only admin users can delete users.")
        }
        db.userDao().delete(user)
    }

    suspend fun getUserById(userId: Int) = db.userDao().getById(userId)

    suspend fun getUserByUsername(username: String) = db.userDao().getByUsername(username)

    fun getAllUsers() = db.userDao().getAllUsers()


    /********************
     * Habit Operations *
     ********************/
    fun getHabitsByUserId(userId: Int) = db.habitDao().getHabitsByUserId(userId)

    fun getHabitsForLoggedInUser(loggedInUserId: Int): Flow<List<Habit>> {
        val loggedInUserId = sharedPrefs.getInt(USER_ID, -1)
        if (loggedInUserId == -1) {
            throw IllegalArgumentException("No user is currently logged in.")
        }
        return db.habitDao().getHabitsByUserId(loggedInUserId)
    }

    fun addHabit(habit: Habit) {
        db.habitDao().insert(habit)
    }


    /***********************
     * HabitLog Operations *
     ***********************/
    fun getHabitLogsForUser(userId: Int) =
        db.habitLogDao().getHabitLogsForUser(userId)

    fun getHabitLogsForLoggedInUser(): Flow<List<HabitLog>> {
        val loggedInUserId = sharedPrefs.getInt(USER_ID, -1)
        if (loggedInUserId == -1) {
            throw IllegalArgumentException("No user is currently logged in.")
        }
        return db.habitLogDao().getHabitLogsForUser(loggedInUserId)
    }


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