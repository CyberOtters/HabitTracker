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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.utils.NormalizedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@Composable
fun HabitWeek(
    habit: Habit,
    weekOfYear: Int
) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val repo = AppRepository.getInstance(context)
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.set(Calendar.WEEK_OF_YEAR, weekOfYear)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val startDate = NormalizedDate.from(Date().apply { time = cal.timeInMillis })
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
    val endDate = NormalizedDate.from(Date().apply { time = cal.timeInMillis })

    val habitLogs = repo.getHabitLogsForHabit(
        habit.habitId,
        startDate,
        endDate
    ).asLiveData()
        .observeAsState(initial = emptyList()).value

    val habitLogsByDate = habitLogs.associateBy { it.date.utcMidnightMillis }


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row() {
            Text(text = habit.name, modifier = Modifier.padding(8.dp))
        }
        Row() {
            (1..7).map { day ->
                cal.set(Calendar.WEEK_OF_YEAR, weekOfYear)
                cal.set(Calendar.DAY_OF_WEEK, day)
                val nd = NormalizedDate.from(Date().apply { time = cal.timeInMillis })
                val log = habitLogsByDate[nd.utcMidnightMillis]
                var color: Color
                if (log == null || log.completed == null) {
                    color = Color.LightGray
                } else if (log.completed) {
                    color = Color.Green
                } else {
                    color = Color.Red
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(color)
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