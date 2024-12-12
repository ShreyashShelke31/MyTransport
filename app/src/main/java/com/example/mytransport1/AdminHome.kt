package com.example.mytransport1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class AdminHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var auth: FirebaseAuth

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)
        val res=findViewById<Button>(R.id.Vres)
        val trip=findViewById<Button>(R.id.Tripdetails)
        val profileImageButton: ImageButton = findViewById(R.id.buttonProfile)
        val TripImageButton: ImageButton = findViewById(R.id.buttonTips)
        val vehicleDetails=findViewById<Button>(R.id.vDetails)
        val buttonDashboard: ImageButton = findViewById(R.id.buttonDashboard)

        // Retrieve the user ID from the Intent extras
        val userId = intent.getStringExtra("USER_ID")
        res.setOnClickListener {
//            val user = auth.currentUser
//            val userId = user?.uid
            val intent = Intent(this,VehicleRegistration::class.java).apply {
                // Put the user ID as an extra in the intent
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
        }
        trip.setOnClickListener {
            val intent = Intent(this,TripDetails::class.java).apply {
                // Put the user ID as an extra in the intent
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
        }
        vehicleDetails.setOnClickListener {
            val intent = Intent(this,ShowVehicles::class.java).apply {
                // Put the user ID as an extra in the intent
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
        }
        profileImageButton.setOnClickListener {
//            val user = auth.currentUser
//            val userId = user?.uid
            val intent = Intent(this,profile::class.java)
            startActivity(intent)
        }
        TripImageButton.setOnClickListener {
//            val user = auth.currentUser
//            val userId = user?.uid
            val intent = Intent(this,ShowTrips::class.java)
            startActivity(intent)
        }
        buttonDashboard.setOnClickListener {
//            val user = auth.currentUser
//            val userId = user?.uid
            val intent = Intent(this,AdminHome::class.java)
            startActivity(intent)
        }
    }
}