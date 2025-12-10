package com.example.habittracker.ui.components

import android.widget.Toast
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import com.example.habittracker.data.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AdminDashboard(repo: AppRepository) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val users = repo.getAllUsers().asLiveData().observeAsState(initial = emptyList()).value

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
                            Button(onClick = {
                                coroutineScope.launch {
                                    try {
                                        repo.deleteUser(user)
                                    } catch (e: IllegalArgumentException) {
                                        Toast.makeText(context, e.message, Toast.LENGTH_LONG)
                                            .show()
                                    }

                                }
                            }) {
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
