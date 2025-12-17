package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
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
import com.example.habittracker.data.HabitLogRow
import com.example.habittracker.utils.NormalizedDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HabitLogActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_USER_ID = "extra_user_id"

        fun intentFactory(context: Context, userId: Int): Intent {
            return Intent(context, HabitLogActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, userId)
            }
        }
    }

    private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_log)

        val userId =
            intent.getIntExtra(EXTRA_USER_ID, -1).takeIf { it > 0 }
                ?: intent.getIntExtra(MainActivity.USER_ID, -1).takeIf { it > 0 }
                ?: intent.getIntExtra("USER_ID", -1).takeIf { it > 0 }
                ?: run { finish(); return }

        val dao = AppDatabase.getDatabase(applicationContext).habitLogDao()

        val recyclerView = findViewById<RecyclerView>(R.id.habitLogRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = HabitLogAdapter(
            formatDate = { nd -> dateFormatter.format(Date(nd.utcMidnightMillis)) },
            onEdit = { row ->
                showEditNoteDialog(row.note) { newNote ->
                    lifecycleScope.launch {
                        val entity: HabitLog = dao.getHabitLogById(row.habitLogId) ?: return@launch
                        dao.updateHabitLog(
                            entity.copy(
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
                dao.getHabitLogRowsForUser(userId).collect { rows: List<HabitLogRow> ->
                    adapter.submitList(rows)
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
        private val formatDate: (NormalizedDate) -> String,
        private val onEdit: (HabitLogRow) -> Unit
    ) : ListAdapter<HabitLogRow, HabitLogAdapter.VH>(Diff()) {

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val dateText: TextView = itemView.findViewById(R.id.dateTextView)
            val activityText: TextView = itemView.findViewById(R.id.activityTextView)
            val noteText: TextView = itemView.findViewById(R.id.noteTextView)
            val editBtn: ImageButton = itemView.findViewById(R.id.editNoteButton)
            val check: ImageView = itemView.findViewById(R.id.completedImageView)
        }

        class Diff : DiffUtil.ItemCallback<HabitLogRow>() {
            override fun areItemsTheSame(oldItem: HabitLogRow, newItem: HabitLogRow) =
                oldItem.habitLogId == newItem.habitLogId

            override fun areContentsTheSame(oldItem: HabitLogRow, newItem: HabitLogRow) =
                oldItem == newItem
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_habit_log, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val row = getItem(position)

            holder.dateText.text = formatDate(row.date)
            holder.activityText.text = row.activity
            holder.noteText.text = row.note ?: ""
            holder.check.visibility = if (row.completed == true) View.VISIBLE else View.GONE

            holder.editBtn.setOnClickListener { onEdit(row) }
        }
    }
}
