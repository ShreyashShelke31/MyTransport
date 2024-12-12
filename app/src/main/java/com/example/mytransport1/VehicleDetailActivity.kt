package com.example.mytransport1

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VehicleDetailActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var rentTextView: TextView
    private lateinit var advanceTextView: TextView
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        // Get vehicle details from the intent
        val vehicleNumber = intent.getStringExtra("VEHICLE_NUMBER") ?: "N/A"
        val driver = intent.getStringExtra("DRIVER") ?: "N/A"

        // Display vehicle details
        findViewById<TextView>(R.id.detailVehicleNumberTextView).text =
            "Vehicle Number: $vehicleNumber"
        findViewById<TextView>(R.id.detailDriverTextView).text = "Driver: $driver"

        // Initialize TextViews for rent and advance totals
        rentTextView = findViewById(R.id.totalRentTextView)
        advanceTextView = findViewById(R.id.totalAdvanceTextView)

        // Fetch trips and calculate totals
        fetchTrips(vehicleNumber)
    }

    private fun fetchTrips(vehicleNumber: String) {
        val currentUser = auth.currentUser
        val userId = currentUser?.email
        if (userId == null) {
            Log.e("VehicleDetailActivity", "No user is logged in.")
            return
        }

        // Fetch trips from the user's collection
        db.collection("USER")
            .document(userId)
            .collection("TRIPS")
            .whereEqualTo("vehicleNo", vehicleNumber)
            .get()
            .addOnSuccessListener { documents ->
                var totalExtraAdv = 0.0
                var totalRemainingrent = 0.0
                var totalBalance = 0.0
                var tripCount = 0

                for (document in documents) {
                    tripCount++ // Increment the trip count for each document
                    val ExtraAdv = document.getDouble("ExtraAdv") ?: 0.0
                    val Remainingrent = document.getDouble("Remainingrent") ?: 0.0

                    val rentString = document.getString("TotalRent") ?: "0.0"
                    val advanceString = document.getString("PayableAdvance") ?: "0.0"

                    val rent = rentString.toDoubleOrNull() ?: 0.0
                    val Advance = advanceString.toDoubleOrNull() ?: 0.0

                    totalExtraAdv += ExtraAdv
                    totalRemainingrent += Remainingrent
                    totalBalance +=(rent-Advance)
                }
                // Display the totals
                findViewById<TextView>(R.id.tripCountTextView).text = "Total Trips: $tripCount"
                findViewById<TextView>(R.id.totalBalanceTextView).text = "Total Earn: $totalBalance"
                rentTextView.text = "Extra Advance: $totalExtraAdv"
                advanceTextView.text = "Remaining rent: $totalRemainingrent"
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Log.e("VehicleDetailActivity", "Error fetching trips: ", exception)
            }
    }
}
