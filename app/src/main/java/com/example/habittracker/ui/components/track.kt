package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun HabitTracker(repo: AppRepository) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val habits =
        repo.getHabitsForLoggedInUser().asLiveData().observeAsState(initial = emptyList()).value
    HabitsList(habits)
}

@Composable
fun HabitsList(habits: List<Habit>, modifier: Modifier = Modifier) {
    if (habits.isEmpty()) {
        Text(text = "No habits recorded yet.")
    } else {
        LazyColumn(modifier = modifier) {
            items(habits) { habit ->
                HabitItem(habit = habit)
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
fun HabitItem(habit: Habit) {
    Card(modifier = Modifier.fillMaxSize()) {
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
        }
    }
}