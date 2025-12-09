package com.example.habittracker.data


import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.habittracker.utils.NormalizedDate
import com.example.habittracker.utils.hashPassword
import java.util.Calendar
import java.util.Date

@Database(
    entities = [User::class, Habit::class, HabitLog::class, HabitCategory::class, HabitCategoryCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun habitCategoryDao(): HabitCategoryDao
    abstract fun habitDao(): HabitDAO

    abstract fun habitLogDao(): HabitLogDao



    companion object {
        const val DATABASE_NAME = "HabitTracker.db"
        const val USER_TABLE_NAME = "User"
        const val HABIT_TABLE_NAME = "Habit"
        const val HABIT_LOG_TABLE_NAME = "HabitLog"

        const val HABIT_CATEGORY_TABLE_NAME = "HabitCategory"

        const val HABIT_CATEGORY_CROSS_REF_TABLE_NAME = "HabitCategoryCrossRef"

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            // remove this line to persist database between app restarts
             context.deleteDatabase(DATABASE_NAME)

            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.i("AppDatabase", "Seeding initial data...")
                            // seed initial users
                            val users = listOf(
                                Pair("admin", "adminpassword"),
                                Pair("user2", "user2password"),
                                Pair("user3", "user3password")
                            )

                            for ((username, password) in users) {
                                val (salt, hash) = hashPassword(password)
                                db.execSQL("INSERT INTO ${USER_TABLE_NAME} (username, passwordSalt, passwordHash, isAdmin) VALUES ('${username}', '${salt}', '${hash}', ${username == "admin"})")
                            }

                            // seed Habits
                            db.execSQL("INSERT INTO ${HABIT_TABLE_NAME} (name, description, points, userId) VALUES ('Drink Water', 'Drink 8 glasses of water', 4, 1)")
                            db.execSQL("INSERT INTO ${HABIT_TABLE_NAME} (name, description, points, userId) VALUES ('Exercise', '30 minutes of exercise', 5, 1)")
                            db.execSQL("INSERT INTO ${HABIT_TABLE_NAME} (name, description, points, userId) VALUES ('Read Book', 'Read for 20 minutes', 5, 1)")

                            // seed HabitLogs
                            for (i in 1..5) {
                                val date = Date().apply {
                                    val cal = Calendar.getInstance()
                                    cal.time = this
                                    cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
                                    this.time = cal.timeInMillis
                                }
                                db.execSQL("INSERT INTO ${HABIT_LOG_TABLE_NAME} (habitId, userId, date, note, completed) VALUES (1, 1, ${NormalizedDate.from(date).utcMidnightMillis}, 'Felt good', 1)")
                            }
                            // seed HabitCategories
                            db.execSQL("INSERT INTO ${HABIT_CATEGORY_TABLE_NAME} (userId, name, description) VALUES (1, 'Health', 'Habits related to health and wellness')")
                            db.execSQL("INSERT INTO ${HABIT_CATEGORY_TABLE_NAME} (userId, name, description) VALUES (1, 'Productivity', 'Habits to boost productivity')")

                            // seed HabitCategoryCrossRefs
                            db.execSQL("INSERT INTO ${HABIT_CATEGORY_CROSS_REF_TABLE_NAME} (habitId, habitCategoryId) VALUES (1, 1)") // Drink Water -> Health
                            db.execSQL("INSERT INTO ${HABIT_CATEGORY_CROSS_REF_TABLE_NAME} (habitId, habitCategoryId) VALUES (2, 1)") // Exercise -> Health
                            db.execSQL("INSERT INTO ${HABIT_CATEGORY_CROSS_REF_TABLE_NAME} (habitId, habitCategoryId) VALUES (3, 2)") // Read Book -> Productivity
                        }
                    }
                )
                .build()
        }
    }
}