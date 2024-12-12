package com.example.mytransport1

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
//import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class KnowVehiclesDatails : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var tripID: String
    private lateinit var trip: Trip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_know_vehicles_datails)

        // Retrieve the trip ID from the intent
        tripID = intent.getStringExtra("TRIP_ID") ?: return

        // Fetch trip details from Firestore
        fetchTripDetails(tripID)

        // Set up buttons
        findViewById<Button>(R.id.editButton).setOnClickListener {
            showEditDialog()
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            showDeleteConfirmationDialog(tripID)
        }
    }

    data class Trip(
        var Origin: String? = null,
        var TripID: String? = null,
        var vehicleNo: String? = null,
        var Destination: String? = null,
        var SDate: String? = null,
        var PaidRent: String? = null,
        var TotalRent: String? = null,
        var PaidAdvance: String? = null,
        var PayableAdvance: String? = null,
        var Remainingrent: Int? = 0,
        var ExtraAdv: Int? = 0,
    )

    private fun fetchTripDetails(tripID: String) {
        val userId = currentUser?.email ?: ""
        val tripRef = db.collection("USER").document(userId).collection("TRIPS").document(tripID)

        tripRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    trip = document.toObject(Trip::class.java) ?: return@addOnSuccessListener
                    displayTripDetails(trip)
                } else {
                    Log.e("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching trip details: ${exception.message}", exception)
            }
    }

    private fun displayTripDetails(trip: Trip) {
        val icon = ContextCompat.getDrawable(this, androidx.constraintlayout.widget.R.drawable.btn_radio_on_mtrl) // Replace with your icon drawable

        findViewById<TextView>(R.id.tripIdTextView).text = createStyledText("Trip ID: ", "${trip.TripID ?: "N/A"}", icon)
        findViewById<TextView>(R.id.originTextView).text = createStyledText("Origin: ", "${trip.Origin ?: "N/A"}", icon)
        findViewById<TextView>(R.id.destinationTextView).text = createStyledText("Destination: ", "${trip.Destination ?: "N/A"}", icon)
        findViewById<TextView>(R.id.dateTextView).text = createStyledText("Date: ", "${trip.SDate ?: "N/A"}", icon)
        findViewById<TextView>(R.id.TotalRentTextView).text = createStyledText("Total Rent: ", "${trip.TotalRent}", icon)
        findViewById<TextView>(R.id.PaidRentTextView).text = createStyledText("Received rent from part: ", "${trip.PaidRent}", icon)
        findViewById<TextView>(R.id.PaidAdvanceTextView).text = createStyledText("Paid Advance to Driver: ", "${trip.PaidAdvance}", icon)
        findViewById<TextView>(R.id.PayableAdvanceTextView).text = createStyledText("Payable Advance: ", "${trip.PayableAdvance}", icon)
        findViewById<TextView>(R.id.remainingRentTextView).text = createStyledText("Paid Advance to Driver: ", "${trip.Remainingrent}", icon)
        findViewById<TextView>(R.id.ExtraAdvanceTextView).text = createStyledText("Payable Advance: ", "${trip.ExtraAdv}", icon)

    }

    private fun createStyledText(title: String, value: String, icon: Drawable?): CharSequence {
        val spannable = SpannableString("$title$value")

        // Set color for title
        spannable.setSpan(ForegroundColorSpan(Color.BLUE), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Change color as needed

        // Set color for value
        spannable.setSpan(ForegroundColorSpan(Color.BLACK), title.length, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Change color as needed

//        // Add icon before title
//        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
//        val imageSpan = ImageSpan(icon, ImageSpan.ALIGN_BASELINE)
//        spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Adjust index to place icon correctly

        return spannable
    }

    private fun showEditDialog() {
        // Show a dialog to edit trip details
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_trip, null)
        val editOrigin = dialogView.findViewById<EditText>(R.id.editOrigin)
        val editDestination = dialogView.findViewById<EditText>(R.id.editDestination)
        val editDate = dialogView.findViewById<EditText>(R.id.editDate)
        val editPaidRent = dialogView.findViewById<EditText>(R.id.editPaidRent)
        val editTotalRent = dialogView.findViewById<EditText>(R.id.editTotalRent)
        val editPaidAdvance = dialogView.findViewById<EditText>(R.id.editPaidAdvance)
        val editPayableAdvance = dialogView.findViewById<EditText>(R.id.editPayableAdvance)

        editOrigin.setText(trip.Origin)
        editDestination.setText(trip.Destination)
        editDate.setText(trip.SDate)
        editPaidRent.setText(trip.PaidRent)
        editTotalRent.setText(trip.TotalRent)
        editPaidAdvance.setText(trip.PaidAdvance)
        editPayableAdvance.setText(trip.PayableAdvance)

        AlertDialog.Builder(this)
            .setTitle("Edit Trip Details")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                showUpdateConfirmationDialog(editOrigin.text.toString(), editDestination.text.toString(), editDate.text.toString(),editPaidRent.text.toString(),editTotalRent.text.toString(),editPaidAdvance.text.toString(),editPayableAdvance.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUpdateConfirmationDialog(newOrigin: String, newDestination: String, newDate: String,newPaidRent:String,newTotalRent:String,newPaidAdvance:String,newPayableAdvance:String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Update")
            .setMessage("Are you sure you want to update the trip details?")
            .setPositiveButton("Yes") { _, _ ->
                updateTrip(newOrigin, newDestination, newDate, newPaidRent,newTotalRent,newPaidAdvance,newPayableAdvance)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateTrip(newOrigin: String, newDestination: String, newDate: String,newPaidRent:String,newTotalRent:String,newPaidAdvance:String,newPayableAdvance:String) {
        val userId = currentUser?.email ?: ""
        val tripRef = db.collection("USER").document(userId).collection("TRIPS").document(tripID)

        // Prepare a map of fields to update
        val updates = hashMapOf<String, Any?>()
        if (newOrigin != trip.Origin) {
            updates["Origin"] = newOrigin
        }
        if (newDestination != trip.Destination) {
            updates["Destination"] = newDestination
        }
        if (newDate != trip.SDate) {
            updates["SDate"] = newDate
        }
        if (newPaidRent != trip.PaidRent) {
            updates["PaidRent"] = newPaidRent
        }
        if (newTotalRent != trip.TotalRent) {
            updates["TotalRent"] = newTotalRent
        }
        if (newPaidAdvance != trip.PaidAdvance) {
            updates["PaidAdvance"] = newPaidAdvance
        }
        if (newPayableAdvance != trip.PayableAdvance) {
            updates["PayableAdvance"] = newPayableAdvance
        }

        // Perform the update
        if (updates.isNotEmpty()) {
            tripRef.update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Trip updated successfully!", Toast.LENGTH_SHORT).show()
                    fetchTripDetails(tripID) // Refresh trip details
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error updating trip: ${exception.message}", exception)
                }
        } else {
            Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog(tripID: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Trip")
            .setMessage("Are you sure you want to delete this trip?")
            .setPositiveButton("Yes") { _, _ ->
                deleteTrip(tripID)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteTrip(tripID: String) {
        val userId = currentUser?.email ?: ""
        val tripRef = db.collection("USER").document(userId).collection("TRIPS").document(tripID)

        tripRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Trip deleted successfully!", Toast.LENGTH_SHORT).show()
                finish() // Go back to the previous activity
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error deleting trip: ${exception.message}", exception)
            }
    }
}
