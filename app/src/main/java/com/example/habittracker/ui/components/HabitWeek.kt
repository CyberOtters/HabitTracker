package com.example.habittracker.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@Composable
fun HabitWeek() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        (1..7).map { day ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_WEEK, day)
                val dayOfWeek = cal.getDisplayName(
                    Calendar.DAY_OF_WEEK,
                    Calendar.SHORT,
                    Locale.getDefault()
                )
                Text(text = dayOfWeek!!)
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(Color.Gray)
                        .border(1.dp, Color.White),
                    contentAlignment = Alignment.Center
                ) {}
            }


        }
    }
}