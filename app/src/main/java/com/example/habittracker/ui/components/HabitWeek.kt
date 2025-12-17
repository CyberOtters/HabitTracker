package com.example.habittracker.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.example.habittracker.HabitLogActivity
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitLog
import com.example.habittracker.utils.NormalizedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@Composable
fun HabitWeek(
    habit: Habit,
    weekOfYear: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val repo = AppRepository.getInstance(context)
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    cal.set(Calendar.WEEK_OF_YEAR, weekOfYear)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val startDate = NormalizedDate.from(Date(cal.timeInMillis))
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
    val endDate = NormalizedDate.from(Date(cal.timeInMillis))

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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = habit.name, modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Habit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Habit")
            }
        }
        Row() {
            (1..7).map { day ->
                cal.set(Calendar.WEEK_OF_YEAR, weekOfYear)
                cal.set(Calendar.DAY_OF_WEEK, day)
                val nd = NormalizedDate.from(Date(cal.timeInMillis))
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
                        .combinedClickable(
                            onClick = {
                                // handle complete toggle
                                if (log == null) {
                                    // create new log with completed = true
                                    coroutineScope.launch {
                                        repo.insertHabitLog(
                                            habit.habitId,
                                            habit.userId,
                                            date = nd,
                                            completed = true
                                        )
                                    }
                                } else {
                                    // update existing log to toggle completed status
                                    coroutineScope.launch {
                                        val completed: Boolean?
                                        if (log.completed == null) {
                                            completed = true
                                        } else if (log.completed) {
                                            completed = false
                                        } else {
                                            completed = null
                                        }
                                        repo.updateHabitLog(
                                            HabitLog(
                                                habitLogId = log.habitLogId,
                                                habitId = log.habitId,
                                                userId = log.userId,
                                                date = log.date,
                                                note = log.note,
                                                completed = completed,
                                                createdAt = log.createdAt,
                                                updatedAt = Date()
                                            )
                                        )
                                    }
                                }
                            },
                            onLongClick = {
                                // start HabitLogActivity to add notes
                                var habitLogId = habitLogsByDate[nd.utcMidnightMillis]?.habitLogId

                                if (habitLogId == null) {
                                    // create a new log entry since none exists
                                    coroutineScope.launch {
                                        habitLogId = repo.insertHabitLog(
                                            habit.habitId,
                                            habit.userId,
                                            date = nd,
                                            completed = null
                                        )
                                        context.startActivity(
                                            HabitLogActivity.intentFactory(
                                                context,
                                                habitLogId
                                            )
                                        )
                                    }
                                } else {
                                    // open existing log entry
                                    context.startActivity(
                                        HabitLogActivity.intentFactory(
                                            context,
                                            habitLogId!!
                                        )
                                    )
                                }


                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {}
            }
        }
    }
}