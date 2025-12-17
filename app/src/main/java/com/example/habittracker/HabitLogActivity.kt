package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
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

        fun intentFactory(context: Context, userId: Int): Intent {
            return Intent(context, HabitLogActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, userId)
            }
        }

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

        val habitId: Int = intent.getIntExtra(EXTRA_HABIT_ID, -1)
        val habitName: String = intent.getStringExtra(EXTRA_HABIT_NAME).orEmpty()

        val isSingleHabit = habitId > 0

        findViewById<TextView>(R.id.titleHabitLogTextView).text =
            if (isSingleHabit && habitName.isNotBlank()) "$habitName Logs" else "Habit Logs"

        val dao = AppDatabase.getDatabase(applicationContext).habitLogDao()

        val recyclerView = findViewById<RecyclerView>(R.id.habitLogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = HabitLogAdapter(
            singleHabitName = habitName.takeIf { isSingleHabit && it.isNotBlank() },
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
                    val listToShow =
                        if (isSingleHabit) allLogs.filter { log -> log.habitId == habitId }
                        else allLogs
                    adapter.submitList(listToShow)
                }
            }
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

    private class HabitLogAdapter(
        private val singleHabitName: String?,
        private val onLongClick: (HabitLog) -> Unit
    ) : ListAdapter<HabitLog, HabitLogAdapter.VH>(Diff()) {

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val dateText: TextView = itemView.findViewById(R.id.dateTextView)
            val activityText: TextView = itemView.findViewById(R.id.activityTextView)
            val check: ImageView = itemView.findViewById(R.id.completedImageView)
        }

        class Diff : DiffUtil.ItemCallback<HabitLog>() {
            override fun areItemsTheSame(oldItem: HabitLog, newItem: HabitLog) =
                oldItem.habitLogId == newItem.habitLogId

            override fun areContentsTheSame(oldItem: HabitLog, newItem: HabitLog) =
                oldItem == newItem
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_habit_log, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val log = getItem(position)

            holder.dateText.text = log.date.toString()
            holder.activityText.text = singleHabitName ?: "Habit ${log.habitId}"
            holder.check.visibility = if (log.completed == true) View.VISIBLE else View.GONE

            holder.itemView.setOnLongClickListener {
                onLongClick(log)
                true
            }
        }
    }
}
