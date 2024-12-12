package com.example.mytransport1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ShowVehicles : AppCompatActivity() {
    private lateinit var vehicleAdapter: VehicleAdapter
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_vehicles)

        val vehicleRecyclerView = findViewById<RecyclerView>(R.id.vehicleRecyclerView)
        vehicleRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create a divider item decoration
        val dividerItemDecoration = DividerItemDecoration(vehicleRecyclerView.context, LinearLayoutManager.VERTICAL)
        vehicleRecyclerView.addItemDecoration(dividerItemDecoration)

        vehicleAdapter = VehicleAdapter { vehicle ->
            // Handle item clicks
            val intent = Intent(this, VehicleDetailActivity::class.java).apply {
                putExtra("VEHICLE_NUMBER", vehicle.Number)
                putExtra("DRIVER", vehicle.Driver)
            }
            startActivity(intent)
        }

        vehicleRecyclerView.adapter = vehicleAdapter
        fetchVehicles()
    }

    private fun fetchVehicles() {
        val userId = currentUser?.email ?: ""
        val vehiclesRef = db.collection("USER").document(userId).collection("vehicles")

        vehiclesRef.get()
            .addOnSuccessListener { querySnapshot ->
                val vehicleList = mutableListOf<Vehicle>()
                for (document in querySnapshot) {
                    val vehicle = document.toObject(Vehicle::class.java)
                    vehicleList.add(vehicle)
                }
                vehicleAdapter.setVehicles(vehicleList)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching vehicles", exception)
            }
    }

    inner class VehicleAdapter(private val onClick: (Vehicle) -> Unit) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

        private var vehicles = listOf<Vehicle>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.showvehicles, parent, false)
            return VehicleViewHolder(view)
        }

        override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
            val vehicle = vehicles[position]
            holder.bind(vehicle)

            holder.itemView.setOnClickListener { onClick(vehicle) }  // Set up click listener

            // Set up delete button
            holder.itemView.findViewById<Button>(R.id.deleteButton).setOnClickListener {
                onDelete(vehicle)
            }
        }

        override fun getItemCount(): Int = vehicles.size

        fun setVehicles(vehicleList: List<Vehicle>) {
            vehicles = vehicleList
            notifyDataSetChanged()
        }

        inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(vehicle: Vehicle) {
                itemView.findViewById<TextView>(R.id.vehicleNameTextView).text = "Vehicle no: ${vehicle.Number}"
                itemView.findViewById<TextView>(R.id.vehicleModelTextView).text = "OWNER: ${vehicle.OwnerName}"
                itemView.findViewById<TextView>(R.id.ownerTextView).text = "Driver: ${vehicle.Driver}"
            }
        }

        private fun onDelete(vehicle: Vehicle) {
            // Show confirmation dialog before deleting
            AlertDialog.Builder(this@ShowVehicles)
                .setTitle("Delete Vehicle")
                .setMessage("Are you sure you want to delete this vehicle?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteVehicle(vehicle)
                }
                .setNegativeButton("No", null)
                .show()
        }

        private fun deleteVehicle(vehicle: Vehicle) {
            // Implement Firestore delete logic here
            val userId = FirebaseAuth.getInstance().currentUser?.email ?: ""
            val vehicleRef = db.collection("USER").document(userId).collection("vehicles").document(vehicle.Number)

            vehicleRef.delete()
                .addOnSuccessListener {
                    // Refresh vehicle list
                    fetchVehicles()

                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error deleting vehicle", exception)
                }
        }
    }
}

data class Vehicle(
    val Driver: String = "",
    val Number: String = "",
    val OwnerName: String =""
)
