package com.example.mytransport1

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TripDetails : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trip_details)

        db = FirebaseFirestore.getInstance()
        // Retrieve the user ID from the Intent extras

        val editTextPartyName = findViewById<EditText>(R.id.PartyName)
        val editTextPartyMob = findViewById<EditText>(R.id.PartyMob)
        val editTextvehicleNo = findViewById<Spinner>(R.id.vehicleNumberSpinner) // Spinner for vehicle numbers
//        val editDriverName = findViewById<Spinner>(R.id.DriverNameSpinner) // Spinner for vehicle numbers

        //val editTextvehicleNo = findViewById<EditText>(R.id.vehicleNo)
        val editTextOrigin = findViewById<EditText>(R.id.Origin)
        val editTextDestination = findViewById<EditText>(R.id.Destination)
        val editTextSDate = findViewById<EditText>(R.id.SDate)
        val editTextSTotalRent = findViewById<EditText>(R.id.TotalRent)
        val editTextPaidRent = findViewById<EditText>(R.id.PaidRent)
        val editTextPayableAdvance = findViewById<EditText>(R.id.PayableAdvance)
        val editTextPaidAdvance = findViewById<EditText>(R.id.PaidAdvance)
        val buttonSave = findViewById<Button>(R.id.TripDetails)
// Fetch available vehicle numbers from Firestore and populate the Spinner
        fetchVehicleNumbers { vehicleNumbers ->
            val vehicleListWithHint = mutableListOf("Select vehicle number") // Add "Select" as a placeholder
            vehicleListWithHint.addAll(vehicleNumbers)

            // Set custom layout for spinner items
            val adapter = ArrayAdapter(this, R.layout.spinner_item, vehicleListWithHint)
            //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vehicleNumbers)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            editTextvehicleNo.adapter = adapter
        }

        buttonSave.setOnClickListener {
            val PartyName = editTextPartyName.text.toString()
            val PartyMob = editTextPartyMob.text.toString()
            val vehicleNo = editTextvehicleNo.selectedItem.toString()
            val Origin = editTextOrigin.text.toString()
            val Destination = editTextDestination.text.toString()
            val SDate = editTextSDate.text.toString()
            val TotalRent = editTextSTotalRent.text.toString()
            val PaidRent = editTextPaidRent.text.toString()
            val PayableAdvance = editTextPayableAdvance.text.toString()
            val PaidAdvance = editTextPaidAdvance.text.toString()
            val Remainingrent =TotalRent.toDouble()-PaidRent.toDouble()
            val ExtraAdv = PayableAdvance.toDouble()-PaidAdvance.toDouble()

            if (PartyName.isNotEmpty() && PartyMob.isNotEmpty() && vehicleNo.isNotEmpty() && Origin.isNotEmpty() && Destination.isNotEmpty() && SDate.isNotEmpty() && TotalRent.isNotEmpty() && PaidRent.isNotEmpty() && PayableAdvance.isNotEmpty() && PaidAdvance.isNotEmpty()) {
                saveTripDetails(PartyName, PartyMob, vehicleNo, Origin,Destination,SDate,TotalRent,PaidRent,PayableAdvance,PaidAdvance,Remainingrent,ExtraAdv)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

        }

    }
    val currentUser = FirebaseAuth.getInstance().currentUser

//    // Function to fetch vehicle numbers and provide them to a callback
    private fun fetchVehicleNumbers(onVehicleNumbersFetched: (List<String>) -> Unit) {
        val userId = currentUser?.email ?: ""
        val vehiclesRef = db.collection("USER").document(userId).collection("vehicles")

        vehiclesRef.get()
            .addOnSuccessListener { querySnapshot ->
                val vehicleNumbers = mutableListOf<String>()

                for (document in querySnapshot) {
                    val vehicleNumber = document.getString("Number") ?: ""
                    vehicleNumbers.add(vehicleNumber)

                }
                onVehicleNumbersFetched(vehicleNumbers)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching vehicle numbers", exception)
            }
    }

    private fun saveTripDetails(PartyName: String, PartyMob: String, vehicleNo: String, Origin: String,Destination:String,SDate:String,TotalRent:String,PaidRent:String,PayableAdvance:String,PaidAdvance:String,Remainingrent:Double,ExtraAdv:Double) {
        // Fetch the last trip ID, increment it, and save the new trip
        val tripCounterRef = db.collection("tripCounter").document("lastTripID")

        tripCounterRef.get()
            .addOnSuccessListener { document ->
                val lastTripID = document?.getLong("id") ?: 0L // Default to 0 if no document found
                val newTripID = lastTripID + 1

                // Update the last trip ID in Firestore
                tripCounterRef.set(mapOf("id" to newTripID))
                val tripID = "${vehicleNo}_$newTripID" // Generate unique TripID
                val tripdetails = hashMapOf(
                    "TripID" to tripID,//"${vehicleNo}_$newTripID", // Generate unique TripID
                    "PartyName" to PartyName,
                    "PartyMob" to PartyMob,
                    "vehicleNo" to vehicleNo,
                    "Origin" to Origin,
                    "Destination" to Destination,
                    "SDate" to SDate,
                    "TotalRent" to TotalRent,
                    "PaidRent" to PaidRent,
                    "PayableAdvance" to PayableAdvance,
                    "PaidAdvance" to PaidAdvance,
                    "Remainingrent" to Remainingrent,
                    "ExtraAdv" to ExtraAdv,
                    "createdAt" to com.google.firebase.Timestamp.now() // Add timestamp
                )
                val userId = intent.getStringExtra("USER_ID")
//        val user = auth.currentUser
//        val userId = user?.uid
                if (userId != null) {
                    db.collection("USER").document(userId).collection("TRIPS")
                        .document(tripID) // Use tripID as the document ID
                        .set(tripdetails).addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Trip details saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            clearForm()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error saving details: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this, "Error saving details", Toast.LENGTH_SHORT).show()

                }
            }

    }

    private fun clearForm() {
        findViewById<EditText>(R.id.PartyName).text.clear()
        findViewById<EditText>(R.id.PartyMob).text.clear()
        //findViewById<EditText>(R.id.vehicleNo).text.clear()
        findViewById<EditText>(R.id.Origin).text.clear()
        findViewById<EditText>(R.id.Destination).text.clear()
        findViewById<EditText>(R.id.SDate).text.clear()
        findViewById<EditText>(R.id.TotalRent).text.clear()
        findViewById<EditText>(R.id.PaidRent).text.clear()
        findViewById<EditText>(R.id.PayableAdvance).text.clear()
        findViewById<EditText>(R.id.PaidAdvance).text.clear()
    }
}



