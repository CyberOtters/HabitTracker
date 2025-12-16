package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.HabitLog
import kotlinx.coroutines.launch
import java.util.Date

class HabitLogActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_USER_ID = "extra_user_id"
        private const val EXTRA_HABIT_ID = "extra_habit_id"
        private const val EXTRA_HABIT_NAME = "extra_habit_name"

        fun intentFactory(context: Context, userId: Int, habitId: Int, habitName: String): Intent {
            return Intent(context, HabitLogActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, userId)
                putExtra(EXTRA_HABIT_ID, habitId)
                putExtra(EXTRA_HABIT_NAME, habitName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_log)

        val userId =
            intent.getIntExtra(EXTRA_USER_ID, -1).takeIf { it > 0 }
                ?: intent.getIntExtra(MainActivity.USER_ID, -1).takeIf { it > 0 }
                ?: intent.getIntExtra("USER_ID", -1).takeIf { it > 0 }
                ?: run { finish(); return }

        val habitId = intent.getIntExtra(EXTRA_HABIT_ID, -1)
        if (habitId <= 0) {
            finish()
            return
        }

        val habitName = intent.getStringExtra(EXTRA_HABIT_NAME).orEmpty()
        findViewById<TextView>(R.id.titleHabitLogTextView).text =
            if (habitName.isNotBlank()) "$habitName Logs" else "Habit Logs"

        val dao = AppDatabase.getDatabase(applicationContext).habitLogDao()

        val recyclerView = findViewById<RecyclerView>(R.id.habitLogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = HabitLogAdapter(
            activityLabel = if (habitName.isNotBlank()) habitName else "Habit $habitId",
            onLongClick = { log ->
                showEditNoteDialog(log.note) { newNote ->
                    lifecycleScope.launch {
                        dao.updateHabitLog(
                            log.copy(
                                note = newNote,
                                updatedAt = Date()
                            )
                        )
                    }
                }
            }
        )
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dao.getHabitLogsForUser(userId).collect { allLogs: List<HabitLog> ->
                    val filtered: List<HabitLog> = allLogs.filter { log -> log.habitId == habitId }
                    adapter.submitList(filtered)
                }
            }
        }
    }
}