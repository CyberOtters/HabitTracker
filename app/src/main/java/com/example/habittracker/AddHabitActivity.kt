package com.example.habittracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.ActivityAddHabitBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitBinding
    private lateinit var repo: AppRepository
    private var userId: Int = -1
    private var habitToEdit: Habit? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHabitBinding.inflate(layoutInflater)
        repo = AppRepository.getInstance(this)
        setContentView(binding.root)

        userId = intent.getIntExtra("USER_ID", -1)
        val habitIdToEdit = intent.getIntExtra("HABIT_ID_TO_EDIT", -1)

        if (habitIdToEdit != -1) {
            lifecycleScope.launch {
                habitToEdit = repo.getHabitById(habitIdToEdit)
                binding.habitNameEditText.setText(habitToEdit?.name)
                binding.habitPointsEditText.setText(habitToEdit?.points.toString())
                binding.habitDescriptionEditText.setText(habitToEdit?.description)
            }
        }

        binding.saveButton.setOnClickListener {
            saveHabit()
        }


    }

    private fun saveHabit() {
        val habitName = binding.habitNameEditText.text.toString()
        val habitPoints = binding.habitPointsEditText.text.toString().toIntOrNull()
        val habitDescription = binding.habitDescriptionEditText.text.toString()

        if (habitName.isBlank() || habitPoints == null || habitDescription.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        if (habitPoints < 0 || habitPoints > 5) {
            Toast.makeText(this, "Points must be between 0 and 5", Toast.LENGTH_LONG).show()
            return
        }

        if (userId == -1) {
            Toast.makeText(
                this,
                "Error: Could not save habit. User ID is missing.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            if (habitToEdit != null) {
                val updatedHabit = habitToEdit!!.copy(
                    name = habitName,
                    points = habitPoints,
                    description = habitDescription
                )
                repo.updateHabit(updatedHabit)
            } else {
                val newHabit = Habit(
                    name = habitName,
                    points = habitPoints,
                    description = habitDescription,
                    userId = userId
                )
                repo.addHabit(newHabit)
            }
            // Switch back to Main thread to show Toast and finish
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddHabitActivity, "Habit Saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }
}