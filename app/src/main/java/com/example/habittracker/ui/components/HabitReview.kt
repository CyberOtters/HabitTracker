package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.example.habittracker.data.AppRepository
import com.example.habittracker.utils.NormalizedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@Composable
fun HabitReview(repo: AppRepository) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val repo = AppRepository.getInstance(context)
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val startDate = NormalizedDate.from(Date(cal.timeInMillis))
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
    val endDate = NormalizedDate.from(Date(cal.timeInMillis))
    val habits =
        repo.getHabitsForLoggedInUser(repo.getLoggedInUserId()).asLiveData()
            .observeAsState(initial = emptyList()).value
    val habitLogs =
        repo.getHabitLogsForDateRange(startDate, endDate).asLiveData()
            .observeAsState(initial = emptyList()).value


    if (habitLogs.isEmpty()) {
        Text(text = "No habit logs for this week.")
        return
    }

    val habitsById = habits.associateBy { it.habitId }

    var completedHabitsCount = 0
    var incompleteHabitsCount = 0
    var totalPoints = 0
    for (log in habitLogs) {
        if (log.completed == true) {
            completedHabitsCount += 1
            totalPoints += habitsById[log.habitId]?.points ?: 0
        } else if (log.completed == false) {
            incompleteHabitsCount += 1
        }
    }

    val totalCount = (habits.size * 7)
    // finds the number of times for the week that habits weren't marked as completed or incomplete
    val skippedHabitsCount = totalCount - (completedHabitsCount + incompleteHabitsCount)
    val averagePoints: Double = if (completedHabitsCount > 0) {
        (totalPoints.toDouble() / completedHabitsCount)
    } else {
        0.0
    }


    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        PieChart(
            values = listOf(
                skippedHabitsCount.toFloat(),
                completedHabitsCount.toFloat(),
                incompleteHabitsCount.toFloat()
            ),
            colors = listOf(Color.LightGray, Color.Green, Color.Red),
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(
                text = "Completed:",
                color = Color.Green,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column() {
            Text(
                text = completedHabitsCount.toString(),
                color = Color.Green,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(
                text = "Incomplete:",
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column() {
            Text(
                text = incompleteHabitsCount.toString(),
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(
                text = "Skipped:",
                color = Color.DarkGray,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column() {
            Text(
                text = skippedHabitsCount.toString(),
                color = Color.DarkGray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(
                text = "Total Points:",
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column() {
            Text(
                text = totalPoints.toString(),
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(
                text = "Average Points:",
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column() {
            Text(
                text = String.format("%.2f", averagePoints),
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

}
