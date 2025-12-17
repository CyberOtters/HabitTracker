package com.example.habittracker.data

import android.content.Context
import android.util.Log
import com.example.habittracker.MainActivity.Companion.SHARED_PREFS_NAME
import com.example.habittracker.MainActivity.Companion.USER_ID
import com.example.habittracker.utils.NormalizedDate
import com.example.habittracker.utils.hashPassword
import kotlinx.coroutines.flow.Flow
import java.util.Date
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

    fun getLoggedInUserId(): Int {
        return sharedPrefs.getInt(USER_ID, -1)
    }

    suspend fun getLoggedInUser(): User? {
        val userId = getLoggedInUserId()
        if (userId == -1) {
            return null
        }
        return getUserById(userId)
    }

    suspend fun updateUserPassword(userId: Int, newPassword: String) {
        val user = getUserById(userId)
            ?: throw IllegalArgumentException("User with ID $userId does not exist.")
        val (newSalt, newHash) = hashPassword(newPassword)
        val updatedUser = user.copy(
            passwordSalt = newSalt,
            passwordHash = newHash
        )
        db.userDao().update(updatedUser)
    }

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

    suspend fun addUser(user: User) {
        db.userDao().insert(user)
    }


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

    suspend fun addHabit(habit: Habit): Long {
        return db.habitDao().insert(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        db.habitDao().delete(habit)
    }

    fun getHabitById(habitId: Int) = db.habitDao().getHabitById(habitId)


    suspend fun updateHabit(habit: Habit) {
        db.habitDao().update(habit)
    }



    /***********************
     * HabitLog Operations *
     ***********************/
    fun getHabitLogsForUser(userId: Int) =
        db.habitLogDao().getHabitLogsForUser(userId)

    fun getHabitLogsForLoggedInUser(): Flow<List<HabitLog>> {
        val loggedInUserId = getLoggedInUserId()
        if (loggedInUserId == -1) {
            throw IllegalArgumentException("No user is currently logged in.")
        }

        return db.habitLogDao().getHabitLogsForUser(loggedInUserId)
    }

    fun getHabitLogsForDateRange(
        startDate: NormalizedDate,
        endDate: NormalizedDate
    ): Flow<List<HabitLog>> {
        val loggedInUserId = getLoggedInUserId()
        if (loggedInUserId == -1) {
            throw IllegalArgumentException("No user is currently logged in.")
        }

        Log.i(
            "AppRepository",
            "Fetching habit logs for userId=${loggedInUserId}, startDate=$startDate, endDate=$endDate"
        )

        return db.habitLogDao().getHabitLogsForDateRange(
            loggedInUserId,
            startDate,
            endDate
        )
    }

    fun getHabitLogsForHabit(
        habitId: Int,
        startDate: NormalizedDate,
        endDate: NormalizedDate
    ): Flow<List<HabitLog>> {
        val loggedInUserId = getLoggedInUserId()
        if (loggedInUserId == -1) {
            throw IllegalArgumentException("No user is currently logged in.")
        }

        Log.i(
            "AppRepository",
            "Fetching habit logs for userId=${loggedInUserId}, habitId=$habitId, startDate=$startDate, endDate=$endDate"
        )

        return db.habitLogDao().getHabitLogsForHabit(
            loggedInUserId,
            habitId,
            startDate,
            endDate
        )
    }

    suspend fun insertHabitLog(
        habitId: Int,
        userId: Int,
        date: NormalizedDate,
        completed: Boolean?
    ) {
        val now = Date()
        db.habitLogDao().insertHabitLog(
            HabitLog(
                habitLogId = 0,
                habitId = habitId,
                userId = userId,
                date = date,
                completed = completed,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun updateHabitLog(habitLog: HabitLog) {
        db.habitLogDao().updateHabitLog(habitLog)
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