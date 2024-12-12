package com.example.mytransport1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ShowTrips : AppCompatActivity() {
    private lateinit var tripAdapter: TripAdapter
    private val db = FirebaseFirestore.getInstance()
    private val vehicles = mutableListOf<String>()
    private var selectedDate: Date? = null
    private lateinit var selectedDateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_trips)

        val vehicleSpinner: Spinner = findViewById(R.id.vehicleSpinner)
        val fetchTripsButton: Button = findViewById(R.id.CheckDetButton)
        val selectDateButton: Button = findViewById(R.id.selectDateButton)
        selectedDateTextView = findViewById(R.id.selectedDateTextView)
        val tripRecyclerView: RecyclerView = findViewById(R.id.tripRecyclerView)

        tripRecyclerView.layoutManager = LinearLayoutManager(this)
        tripAdapter = TripAdapter { trip -> onTripClicked(trip) }
        tripRecyclerView.adapter = tripAdapter
        tripRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        fetchVehicleNumbers { vehicleNumbers ->
            val vehicleListWithHint = mutableListOf("Select Vehicle No").apply {
                addAll(vehicleNumbers)
            }
            val adapter = ArrayAdapter(this, R.layout.spinner_item, vehicleListWithHint)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            vehicleSpinner.adapter = adapter
        }

        selectDateButton.setOnClickListener { showDatePickerDialog() }

        fetchTripsButton.setOnClickListener {
            val selectedVehicle = vehicleSpinner.selectedItem.toString()
            if (selectedVehicle != "Select Vehicle No") {
                fetchTrips(selectedVehicle, selectedDate)
            }
        }
    }

    private val currentUser = FirebaseAuth.getInstance().currentUser

    private fun fetchVehicleNumbers(onVehicleNumbersFetched: (List<String>) -> Unit) {
        val userId = currentUser?.email ?: ""
        val vehiclesRef = db.collection("USER").document(userId).collection("vehicles")

        vehiclesRef.get()
            .addOnSuccessListener { querySnapshot ->
                val vehicleNumbers = querySnapshot.documents.mapNotNull { it.getString("Number") }
                onVehicleNumbersFetched(vehicleNumbers)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching vehicle numbers", exception)
            }
    }

    private fun fetchTrips(vehicleNumber: String, date: Date?) {
        val userId = currentUser?.email ?: ""
        val tripsRef = db.collection("USER").document(userId).collection("TRIPS")

        if (date != null) {
            val calendar = Calendar.getInstance().apply { time = date }
            val startDate = com.google.firebase.Timestamp(calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.time)

            val endDate = com.google.firebase.Timestamp(calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.time)

            tripsRef.whereEqualTo("vehicleNo", vehicleNumber)
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tripList = querySnapshot.documents.mapNotNull { it.toObject(Trip::class.java) }
                    tripAdapter.setTrips(tripList)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching trips: ${exception.message}", exception)
                }
        } else {
            tripsRef.whereEqualTo("vehicleNo", vehicleNumber)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tripList = querySnapshot.documents.mapNotNull { it.toObject(Trip::class.java) }
                    tripAdapter.setTrips(tripList)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching trips: ${exception.message}", exception)
                }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, selectedYear)
                set(Calendar.MONTH, selectedMonth)
                set(Calendar.DAY_OF_MONTH, selectedDay)
            }.time

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDateTextView.text = "Selected Date: ${dateFormat.format(selectedDate)}"
        }, year, month, day).show()
    }

    private fun onTripClicked(trip: Trip) {
        val intent = Intent(this, KnowVehiclesDatails::class.java).apply {
            putExtra("TRIP_ID", trip.TripID)
            putExtra("ORIGIN", trip.Origin)
            putExtra("DESTINATION", trip.Destination)
            putExtra("DATE", trip.SDate)
        }
        startActivity(intent)
    }
}

data class Trip(
    val Origin: String? = null,
    val TripID: String? = null,
    val vehicleNo: String? = null,
    val Destination: String? = null,
    val SDate: String? = null
)

class TripAdapter(private val onTripClicked: (Trip) -> Unit) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {
    private var trips = listOf<Trip>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.showtrips, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(trips[position])
    }

    override fun getItemCount(): Int = trips.size

    fun setTrips(tripList: List<Trip>) {
        trips = tripList
        notifyDataSetChanged()
    }

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(trip: Trip) {
            itemView.findViewById<TextView>(R.id.tripNameTextView).text = "Trip ID: ${trip.TripID ?: "N/A"}"
            itemView.findViewById<TextView>(R.id.tripModelTextView).text = "Origin: ${trip.Origin ?: "N/A"}"
            itemView.findViewById<TextView>(R.id.textViewDate).text = "D: ${trip.SDate ?: "N/A"}"
            itemView.findViewById<TextView>(R.id.textviewDestination).text = "Destination: ${trip.Destination ?: "N/A"}"

            itemView.setOnClickListener {
                onTripClicked(trip)
            }
        }
    }
}
