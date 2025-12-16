package com.example.habittracker.ui.components

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

    Text(text = "Habit logs count: ${habitLogs.size}")

    var completedHabitsCount = 0
    var incompleteHabitsCount = 0
    for (log in habitLogs) {
        if (log.completed == true) {
            completedHabitsCount += 1
        } else if (log.completed == false) {
            incompleteHabitsCount += 1
        }
    }

    // finds the number of times for the week that habits weren't marked as completed or incomplete
    val skippedHabitsCount = (habits.size * 7) - (completedHabitsCount + incompleteHabitsCount)



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
