package com.example.habittracker
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.HabitLog

class HabitLogActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_DATE_MILLIS = "extra_date_millis"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_log)

        val habitId = intent.getIntExtra(EXTRA_HABIT_ID, -1)
        val userId = intent.getIntExtra(EXTRA_USER_ID, -1)
        val dateMillis = intent.getLongExtra(EXTRA_DATE_MILLIS, -1L)

        if (habitId <= 0 || userId <= 0 || dateMillis <= 0L) {
            Toast.makeText(this, "Missing habit log info", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
}
