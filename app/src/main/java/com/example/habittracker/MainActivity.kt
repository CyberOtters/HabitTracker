package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.Habit
import com.example.habittracker.data.HabitLog
import com.example.habittracker.data.User
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.ui.components.AdminDashboard
import com.example.habittracker.ui.components.HabitReview
import com.example.habittracker.ui.components.HabitTracker
import com.example.habittracker.ui.components.UserProfile
import com.example.habittracker.ui.theme.HabitTrackerTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    companion object {
        const val USER_ID = "com.example.habittracker.USER_ID"
        fun createIntent(context: Context, userId: Int): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(USER_ID, userId)
            return intent
        }
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var repo: AppRepository

    private lateinit var sharedPrefs: SharedPreferences

    private var loggedInUserId = -1

    private var loggedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repo = AppRepository.getInstance(this)
        sharedPrefs = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE)

        launchApp()
    }

    fun launchApp() {
        if (intent.hasExtra(USER_ID)) {
            loggedInUserId = intent.getIntExtra(USER_ID, -1)
            // save to shared preferences
            sharedPrefs.edit {
                putInt(USER_ID, loggedInUserId)
            }
        } else if (sharedPrefs.contains(USER_ID)) {
            loggedInUserId = sharedPrefs.getInt(USER_ID, -1)
        }

        if (loggedInUserId == -1) {
            val intent = LoginActivity.createIntent(this)
            startActivity(intent)
            finish()
        } else {
            lifecycleScope.launch {
                val user = repo.getUserById(loggedInUserId)
                if (user == null) {
                    // user not found, redirect to login
                    val intent = LoginActivity.createIntent(this@MainActivity)
                    startActivity(intent)
                    finish()
                } else {
                    loggedInUser = user
                    val habits = repo.getHabitByUserId(loggedInUserId)
                    val habitLogs = repo.getHabitLogsForUser(loggedInUserId)
                    var allUsers = emptyList<User>()
                    if (user.isAdmin) {
                        allUsers = repo.getAllUsers()
                    }

                    setContent {
                        HabitTrackerTheme {
                            MainNav(
                                repo,
                                user = loggedInUser as User,
                                allUsers = allUsers,
                                habits = habits,
                                habitLogs = habitLogs,
                                handleLogout = { logoutUser() }
                            )
                        }
                    }
                }
            }
        }
    }


    fun logoutUser() {
        loggedInUserId = -1
        sharedPrefs.edit {
            remove(USER_ID)
        }
        val intent = LoginActivity.createIntent(this)
        startActivity(intent)
        finish()
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    TRACK("Track", Icons.Default.Check),
    REVIEW("Review", Icons.Default.BarChart),
    PROFILE("Profile", Icons.Default.AccountBox),
}


@Composable
fun MainNav(
    repo: AppRepository,
    user: User,
    allUsers: List<User>,
    habits: List<Habit>,
    habitLogs: List<HabitLog>,
    handleLogout: () -> Unit
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TRACK) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Greeting(
                    name = user.username,
                    modifier = Modifier.padding(innerPadding)
                )
                Spacer(modifier = Modifier.padding(16.dp))
                when (currentDestination) {
                    AppDestinations.TRACK -> HabitTracker(
                        habits,
                        modifier = Modifier.padding(16.dp)
                    )

                    AppDestinations.REVIEW -> HabitReview(habitLogs)
                    AppDestinations.PROFILE -> {
                        UserProfile(user)
                        if (user.isAdmin) {
                            AdminDashboard(repo, allUsers)
                        }
                    }
                }

                LogoutButton(handleLogout = handleLogout)
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun LogoutButton(handleLogout: () -> Unit) {
    Button(
        onClick = handleLogout
    ) {
        Text("Logout")
    }
}
