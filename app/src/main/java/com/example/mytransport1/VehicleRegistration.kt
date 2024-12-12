package com.example.mytransport1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class VehicleRegistration : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_registration)
        db = FirebaseFirestore.getInstance()
        // Retrieve the user ID from the Intent extras

        val editTextOwnerName = findViewById<EditText>(R.id.ownerName)
        val editTextNumber = findViewById<EditText>(R.id.vehicleNo)
        val editTextModel = findViewById<EditText>(R.id.vehicleModel)
        val editTextDriver = findViewById<EditText>(R.id.DriverName)
        val buttonSave = findViewById<Button>(R.id.RegisterVehicle)

        buttonSave.setOnClickListener {
            val OwnerName = editTextOwnerName.text.toString()
            val Number = editTextNumber.text.toString()
            val Model = editTextModel.text.toString()
            val Driver = editTextDriver.text.toString()

            if (OwnerName.isNotEmpty() && Number.isNotEmpty() && Model.isNotEmpty() && Driver.isNotEmpty()) {
                saveVehicleDetails(OwnerName, Number, Model, Driver)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveVehicleDetails(OwnerName: String, Number: String, Model: String, Driver: String) {
        val vehicle = hashMapOf(
            "OwnerName" to OwnerName,
            "Number" to Number,
            "Model" to Model,
            "Driver" to Driver,
            "createdAt" to com.google.firebase.Timestamp.now() // Add timestamp

        )
        val userId = intent.getStringExtra("USER_ID")
//        val user = auth.currentUser
//        val userId = user?.uid
        if (userId != null) {
            db.collection("USER").document(userId).collection("vehicles")
                .document(Number)  // Set the document ID to the vehicle number
                .set(vehicle)
                .addOnSuccessListener {
                    Toast.makeText(this, "Vehicle details saved successfully", Toast.LENGTH_SHORT).show()
                    clearForm()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else{
            Toast.makeText(this, "Error saving details", Toast.LENGTH_SHORT).show()

        }
    }



    private fun clearForm() {
        findViewById<EditText>(R.id.ownerName).text.clear()
        findViewById<EditText>(R.id.vehicleNo).text.clear()
        findViewById<EditText>(R.id.vehicleModel).text.clear()
        findViewById<EditText>(R.id.DriverName).text.clear()
    }
}



