package com.example.mytransport1

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class profile : AppCompatActivity() {
    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonSelectImage: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextTRname: EditText
    private lateinit var editTextMobileNo: EditText
    private lateinit var buttonSaveChanges: Button
    private lateinit var buttonCancel: Button
    private var imageUri: Uri? = null
    private lateinit var Auth: FirebaseAuth

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                imageUri?.let {
                    try {
                        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        imageViewProfile.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        imageViewProfile = findViewById(R.id.imageViewProfile )
        buttonSelectImage = findViewById(R.id.buttonSelectImage)
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextTRname = findViewById(R.id.editTextPassword)
        editTextMobileNo = findViewById(R.id.editTextConfirmPassword)
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges)
        buttonCancel = findViewById(R.id.buttonCancel)

        Auth = FirebaseAuth.getInstance()

        // Load existing user data
        loadUserProfile()

        buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        buttonSaveChanges.setOnClickListener {
            saveProfileChanges()
        }

        buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadUserProfile() {
        val user: FirebaseUser? = Auth.currentUser
        user?.let {
            val email = it.email
            editTextEmail.setText(email)
            // You can also set other user data such as profile name here
        }
    }

    private fun saveProfileChanges() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val TRname = editTextTRname.text.toString().trim()
        val MobileNo = editTextMobileNo.text.toString().trim()

        if (validateForm(name, email, TRname, MobileNo)) {
            // Save changes to your data source or update Firebase
        }
    }

    private fun validateForm(name: String, email: String, TRname: String, MobileNo: String): Boolean {
        // Perform validation
        return name.isNotEmpty() && email.isNotEmpty() && TRname.isNotEmpty() && MobileNo.isNotEmpty()
    }
}


