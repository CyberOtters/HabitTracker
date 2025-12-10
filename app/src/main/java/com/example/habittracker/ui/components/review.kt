package com.example.habittracker.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.asLiveData
import com.example.habittracker.data.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun HabitReview(repo: AppRepository) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val habitLogs =
        repo.getHabitLogsForLoggedInUser().asLiveData().observeAsState(initial = emptyList()).value
    Text(text = "Habit logs count: ${habitLogs.size}")
}
