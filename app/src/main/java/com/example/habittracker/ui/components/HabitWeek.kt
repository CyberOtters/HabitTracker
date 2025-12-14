package com.example.habittracker.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun HabitWeek(habit: Habit, habitLogs: List<HabitLog> = emptyList()) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val repo = AppRepository.getInstance(context)


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row() {
            Text(text = habit.name, modifier = Modifier.padding(8.dp))
        }
        Row() {
            (1..7).map { day ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color.Gray)
                        .border(1.dp, Color.White)
                        .clickable {
                            // Handle habit completion toggle here

                        },
                    contentAlignment = Alignment.Center,
                ) {}
            }
        }
    }
}