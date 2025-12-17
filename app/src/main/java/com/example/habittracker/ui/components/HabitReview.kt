package com.example.habittracker.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.example.habittracker.HabitLogActivity
import com.example.habittracker.data.AppRepository

@Composable
fun HabitReview(repo: AppRepository, userId: Int) {
    val context = LocalContext.current
    val habitLogs =
        repo.getHabitLogsForLoggedInUser().asLiveData().observeAsState(initial = emptyList()).value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Habit logs count: ${habitLogs.size}")

        Card(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { },
                    onLongClick = {
                        context.startActivity(HabitLogActivity.intentFactory(context, userId))
                    }
                )
        ) {
            Text(text = "Long-press to open Habit Logs", modifier = Modifier.padding(16.dp))
        }
    }
}
