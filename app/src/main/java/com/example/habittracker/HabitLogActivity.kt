package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.AppRepository
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitLogBinding.inflate(layoutInflater)
        repo = AppRepository.getInstance(this)
        setContentView(binding.root)

        val userId = repo.getLoggedInUserId()
        if (userId == -1) {
            throw IllegalStateException("No logged in user")
        }



        lifecycleScope.launch {

        }
    }

    private fun showEditNoteDialog(initialNote: String?, onSave: (String?) -> Unit) {
        val editText = EditText(this).apply {
            setText(initialNote.orEmpty())
            hint = "Enter Note..."
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Note")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val cleaned = editText.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
                onSave(cleaned)
            }
            .show()
    }

}
