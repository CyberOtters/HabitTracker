package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.User
import com.example.habittracker.ui.components.AdminDashboard
import com.example.habittracker.ui.components.HabitReview
import com.example.habittracker.ui.components.HabitTracker
import com.example.habittracker.ui.components.UserProfile
import com.example.habittracker.ui.theme.HabitTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        const val USER_ID = "com.example.habittracker.USER_ID"
        const val SHARED_PREFS_NAME = "HabitTrackerPrefs"

        fun createIntent(context: Context, userId: Int): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(USER_ID, userId)
            }
        }
    }

    private lateinit var repo: AppRepository
    private lateinit var sharedPrefs: SharedPreferences

    private var loggedInUserId = -1
    private var loggedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repo = AppRepository.getInstance(this)
        sharedPrefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

        launchApp()
    }

    private fun launchApp() {
        if (intent.hasExtra(USER_ID)) {
            loggedInUserId = intent.getIntExtra(USER_ID, -1)
            sharedPrefs.edit { putInt(USER_ID, loggedInUserId) }
        } else if (sharedPrefs.contains(USER_ID)) {
            loggedInUserId = sharedPrefs.getInt(USER_ID, -1)
        }

        if (loggedInUserId == -1) {
            startActivity(LoginActivity.createIntent(this))
            finish()
            return
        }

        lifecycleScope.launch {
            val user = repo.getUserById(loggedInUserId)
            if (user == null) {
                startActivity(LoginActivity.createIntent(this@MainActivity))
                finish()
                return@launch
            }

            loggedInUser = user

            setContent {
                HabitTrackerTheme {
                    MainNav(
                        repo = repo,
                        user = user,
                        handleLogout = { logoutUser() }
                    )
                }
            }
        }
    }

    private fun logoutUser() {
        loggedInUserId = -1
        sharedPrefs.edit { remove(USER_ID) }
        startActivity(LoginActivity.createIntent(this))
        finish()
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    TRACK("Track", Icons.Filled.Check),
    REVIEW("Review", Icons.Filled.BarChart),
    PROFILE("Profile", Icons.Filled.AccountBox),
}

@Composable
fun MainNav(
    repo: AppRepository,
    user: User,
    handleLogout: () -> Unit
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TRACK) }
    val context = LocalContext.current

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { dest ->
                item(
                    icon = { Icon(dest.icon, contentDescription = dest.label) },
                    label = { Text(dest.label) },
                    selected = dest == currentDestination,
                    onClick = { currentDestination = dest }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                if (currentDestination == AppDestinations.TRACK) {
                    FloatingActionButton(onClick = {
                        val intent = Intent(context, AddHabitActivity::class.java).apply {
                            putExtra("USER_ID", user.userId)
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Greeting(name = user.username, modifier = Modifier.padding(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    when (currentDestination) {
                        AppDestinations.TRACK -> HabitTracker(repo, user.userId)
                        AppDestinations.REVIEW -> HabitReview(repo, user.userId)
                        AppDestinations.PROFILE -> {
                            UserProfile(user)
                            if (user.isAdmin) AdminDashboard(repo)
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
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun LogoutButton(handleLogout: () -> Unit) {
    Button(onClick = handleLogout) {
        Text("Logout")
    }
}
