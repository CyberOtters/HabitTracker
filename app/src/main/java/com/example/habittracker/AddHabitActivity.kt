package com.example.habittracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.ActivityAddHabitBinding
import com.example.habittracker.utils.NormalizedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitBinding
    private lateinit var repo: AppRepository
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = AppRepository.getInstance(this)
        userId = intent.getIntExtra("USER_ID", -1)

        binding.saveButton.setOnClickListener {
            saveHabit()
        }
    }

    private fun saveHabit() {
        val habitName = binding.habitNameEditText.text.toString().trim()
        val habitPoints = binding.habitPointsEditText.text.toString().toIntOrNull()
        val habitDescription = binding.habitDescriptionEditText.text.toString().trim()

        if (habitName.isBlank() || habitPoints == null || habitDescription.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        if (userId == -1) {
            Toast.makeText(this, "Error: Could not save habit. User ID is missing.", Toast.LENGTH_LONG).show()
            return
        }

        val newHabit = Habit(
            name = habitName,
            points = habitPoints,
            description = habitDescription,
            userId = userId
        )

        CoroutineScope(Dispatchers.IO).launch {
            val newHabitId = repo.addHabit(newHabit).toInt()

            repo.insertHabitLog(
                habitId = newHabitId,
                userId = userId,
                date = NormalizedDate.from(Date()),
                completed = null
            )

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddHabitActivity, "Habit Saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
