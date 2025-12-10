package com.example.habittracker.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.habittracker.data.HabitLog

@Composable
fun HabitReview(habitLogs: List<HabitLog>,) {
    Text(text = "Habit logs count: ${habitLogs.size}")
}
