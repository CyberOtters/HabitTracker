package com.example.habittracker.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.habittracker.data.User


@Composable
fun AdminDashboard(users: List<User>,) {
    Text(text = "This is where the admin can do admin stuff.")
}
