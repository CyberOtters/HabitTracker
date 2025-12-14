package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.asLiveData
import com.example.habittracker.data.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import java.util.Locale

@Composable
fun HabitTracker(repo: AppRepository, userId: Int) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val habits =
        repo.getHabitsForLoggedInUser(userId).asLiveData()
            .observeAsState(initial = emptyList()).value
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        val cal = Calendar.getInstance()
        (1..7).map { day ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                cal.set(Calendar.DAY_OF_WEEK, day)
                val dayOfWeek = cal.getDisplayName(
                    Calendar.DAY_OF_WEEK,
                    Calendar.SHORT,
                    Locale.getDefault()
                )
                Text(text = dayOfWeek!!)
            }
        }
    }
    for (habit in habits) {
//        val habitLogs =
//            repo.getHabitLogsForHabit(habit.id).asLiveData()
//                .observeAsState(initial = emptyList()).value
        HabitWeek(habit = habit)
    }
}