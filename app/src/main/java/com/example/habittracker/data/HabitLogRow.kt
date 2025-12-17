package com.example.habittracker.data

import com.example.habittracker.utils.NormalizedDate

data class HabitLogRow(
    val habitLogId: Int,
    val date: NormalizedDate,
    val activity: String,
    val completed: Boolean?,
    val note: String?
)
