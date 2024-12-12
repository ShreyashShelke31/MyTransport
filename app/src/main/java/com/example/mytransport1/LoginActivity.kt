package com.example.mytransport1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var errorTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonlogin)
        errorTextView = findViewById(R.id.textViewError)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            errorTextView.text = "Email and Password cannot be empty."
            errorTextView.visibility = TextView.VISIBLE
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.email

                        val intent = Intent(this, AdminHome::class.java).apply {
                            // Put the user ID as an extra in the intent
                            putExtra("USER_ID", userId)
                        }
                        startActivity(intent)

                        // For example: startActivity(Intent(this, MainActivity::class.java))
                        errorTextView.visibility = TextView.GONE
                        // Handle successful login
                    } else {
                        // If login fails, display a message to the user
                        errorTextView.text = "Authentication failed: ${task.exception?.message}"
                        errorTextView.visibility = TextView.VISIBLE
                    }
                }

            }

    }

