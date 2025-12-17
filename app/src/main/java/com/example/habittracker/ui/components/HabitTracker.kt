package com.example.habittracker.ui.components

import android.content.Intent
import androidx.activity.result.launch
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
import com.example.habittracker.AddHabitActivity
import com.example.habittracker.data.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.launch

@Composable
fun HabitTracker(repo: AppRepository, userId: Int) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val habits =
        repo.getHabitsForLoggedInUser(userId).asLiveData()
            .observeAsState(initial = emptyList()).value
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val weekOfYear = cal.get(Calendar.WEEK_OF_YEAR)

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
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
        HabitWeek(
            habit,
            weekOfYear,
            onEdit = {
                val intent = Intent(context, AddHabitActivity::class.java).apply {
                    putExtra("USER_ID", userId)
                    putExtra("HABIT_ID_TO_EDIT", habit.habitId)
                }
                context.startActivity(intent)
            },
            onDelete = {
                coroutineScope.launch {
                    repo.deleteHabit(habit)
                }
            }
        )
    }
}