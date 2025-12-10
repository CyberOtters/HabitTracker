package com.example.habittracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.User


@Composable
fun AdminDashboard(users: List<User>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(users) { user ->
                Card(
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${user.userId} - ${user.username}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Column() {
                            Button(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete User"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
