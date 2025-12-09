package com.example.habittracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.AppRepository
import com.example.habittracker.databinding.ActivityLoginBinding
import com.example.habittracker.utils.verifyPassword
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var repo: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        repo = AppRepository.getInstance(this)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            lifecycleScope.launch {
                login()
            }
        }
    }

    private suspend fun login() {
        val username = binding.userNameLoginEditText.text.toString()
        val password = binding.passwordLoginEditText.text.toString()

        Log.i("LoginActivity", "Attempting login for user: $username")

        val user = repo.getUserByUsername(username)
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
        } else if (verifyPassword(password, user.passwordSalt, user.passwordHash)) {
            val intent = MainActivity.createIntent(this, user.userId)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show()
        }
    }

    // use a companion object for static methods in Kotlin
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}