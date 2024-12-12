package com.example.mytransport1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class registration : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reristration)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        val registerButton=findViewById<Button>(R.id.buttonRegister)
        val usernmEditText=findViewById<EditText>(R.id.editTextUsername)
        val emailEditText=findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText=findViewById<EditText>(R.id.editTextPassword)

        registerButton.setOnClickListener {
            val usernm = emailEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()


            registerUser(usernm, email, password)
        }
    }
        private fun registerUser(usernm:String,email:String,password:String) {

            // Basic validation
            if (usernm.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password or name", Toast.LENGTH_SHORT).show()
                return
            }

            // Register user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration success
                       Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        val userId = auth.currentUser?.uid
                        registerUser(usernm, email, password)
                        // TODO: Navigate to another activity or handle user information
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If registration fails
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

             fun saveUserInfo(userId: String?,usernm: String, email: String, password: String) {
                if (userId != null) {
                    val user = User(usernm, email, password)
                    val database = FirebaseDatabase.getInstance()
                    val userRef = database.getReference("users").child(userId)

                    userRef.setValue(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "User registered successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Navigate to the next screen or perform other actions
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to save user info: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
data class User(val usernm:String,val email:String,val password:String)





