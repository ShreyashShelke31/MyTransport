package com.example.mytransport1


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val register = findViewById<Button>(R.id.btnRegister)
        val login =findViewById<Button>(R.id.BtnLogin)
//        val Ahome =findViewById<Button>(R.id.BtnAhome)

        register.setOnClickListener {
            // Create an Intent to start the RegistrationActivity
            val intent = Intent(this,registration::class.java)
            startActivity(intent)
        }
        login.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
//        Ahome.setOnClickListener {
//            val intent = Intent(this,AdminHome::class.java)
//            startActivity(intent)
//        }
    }
}