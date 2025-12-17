package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitLog
import com.example.habittracker.databinding.ActivityHabitLogBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HabitLogActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_HABIT_LOG_ID = "com.example.habittracker.EXTRA_HABIT_LOG_ID"

        fun intentFactory(context: Context, habitLogId: Int): Intent {
            return Intent(context, HabitLogActivity::class.java).apply {
                putExtra(EXTRA_HABIT_LOG_ID, habitLogId)
            }
        }
    }

    private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private lateinit var binding: ActivityHabitLogBinding
    private lateinit var repo: AppRepository

    private lateinit var habitLog: HabitLog
    private lateinit var habit: Habit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitLogBinding.inflate(layoutInflater)
        repo = AppRepository.getInstance(this)
        val habitLogId = intent.getIntExtra(EXTRA_HABIT_LOG_ID, -1)
        val userId = repo.getLoggedInUserId()
        if (userId == -1) {
            throw IllegalStateException("No logged in user")
        }

        binding.saveNoteButton.setOnClickListener {
            val note = binding.noteEditText.text.toString()
            saveNote(habitLogId, note)
            finish()
        }

        lifecycleScope.launch {
            habitLog = repo.getHabitLogById(habitLogId).let { habitLog ->
                habitLog ?: throw IllegalArgumentException("No HabitLog found with ID $habitLogId")
            }
            habit = repo.getHabitById(habitLog.habitId).let { habit ->
                habit
                    ?: throw IllegalArgumentException("No Habit found with ID ${habitLog.habitId}")
            }

            binding.habitName.setText(habit.name)
            binding.noteEditText.setText(habitLog.note)
            binding.dateTextView.setText(dateFormatter.format(habitLog.date.toDate()))
            setContentView(binding.root)
        }


    }

    private fun saveNote(habitLogId: Int, note: String) {
        lifecycleScope.launch {
            val updatedLog = habitLog.copy(note = note)
            repo.updateHabitLog(updatedLog)
        }
    }
}
