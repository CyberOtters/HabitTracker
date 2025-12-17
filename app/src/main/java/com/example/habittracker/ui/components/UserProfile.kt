package com.example.habittracker.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.User
import com.example.habittracker.utils.verifyPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UserProfile(user: User) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    val context = LocalContext.current
    val repo = AppRepository.getInstance(context)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        var password by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmNewPassword by remember { mutableStateOf("") }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Enter your current password") }
        )
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Enter your new password") }
        )
        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Confirm your new password") }
        )
        Button(modifier = Modifier.padding(8.dp), onClick = {
            coroutineScope.launch {
                // Check if current password matches
                val user = repo.getLoggedInUser()
                if (user == null) {
                    Toast.makeText(context, "User not found", Toast.LENGTH_LONG).show()
                } else if (verifyPassword(password, user.passwordSalt, user.passwordHash)) {
                    // Current password is correct, proceed to update
                    if (newPassword != confirmNewPassword) {
                        Toast.makeText(context, "New passwords do not match", Toast.LENGTH_LONG)
                            .show()
                        return@launch
                    }
                    try {
                        repo.updateUserPassword(user.userId, newPassword)
                        password = ""
                        newPassword = ""
                        confirmNewPassword = ""
                        Toast.makeText(
                            context,
                            "Password updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error updating password: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    Toast.makeText(context, "Invalid password", Toast.LENGTH_LONG).show()
                }
            }

        }) {
            Text("Update Password")
        }
    }


}

