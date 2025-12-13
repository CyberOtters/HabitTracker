package com.example.habittracker.ui.components

import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HabitTracker(repo: AppRepository, userId: Int) {

    val habits by repo.getHabitsByUserId(userId).asLiveData().observeAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    HabitsList(
        habits = habits,
        onDeleteHabit = { habit ->
            // --- FIX 2: Explicitly use the IO dispatcher for the database operation ---
            coroutineScope.launch(Dispatchers.IO) {
                repo.deleteHabit(habit)
            }
        }
    )
}

@Composable
fun HabitsList(habits: List<Habit>,
               modifier: Modifier = Modifier,
               onDeleteHabit: (Habit) -> Unit
) {
    if (habits.isEmpty()) {
        Text(text = "No habits recorded yet.")
    } else {
        LazyColumn(modifier = modifier) {
            items(habits) { habit ->
                HabitItem(
                    habit = habit,
                    onDeleteClick = { onDeleteHabit(habit) }
                )
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
fun HabitItem(habit: Habit, onDeleteClick: () -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Habit") },
            text = { Text("Are you sure you want to delete the habit '${habit.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Habit: ${habit.name}")
                Text(text = "Description: ${habit.description}")
                Text(text = "Points: ${habit.points}")
                Text(text = "Date: ${habit.createdAt}")
            }
            // Add Edit Icon Button
            IconButton(onClick = { /* TODO: Handle Edit Click */ }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Habit"
                )
            }

            // Add Delete Icon Button
            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Habit"
                )
            }
        }
    }
}