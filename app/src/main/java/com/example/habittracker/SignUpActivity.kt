package com.example.habittracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.habittracker.data.AppRepository
import com.example.habittracker.data.User
import com.example.habittracker.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.habittracker.utils.hashPassword

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var repo: AppRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = AppRepository.getInstance(this)

        binding.registerButton.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {
        val username = binding.userNameRegisterEditText.text.toString().trim()
        val password = binding.passwordLoginEditText.text.toString().trim()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val existingUser = repo.getUserByUsername(username)
            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignUpActivity, "Username already taken", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val (salt, hash) = hashPassword(password)
            val newUser = User(username = "test", isAdmin = false, passwordHash = hash, passwordSalt = salt)
            repo.addUser(newUser)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@SignUpActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}