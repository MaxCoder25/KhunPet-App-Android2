package com.example.khunpet.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.khunpet.R
import com.example.khunpet.databinding.ActivityDatosUserBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class DataUserActivity:  AppCompatActivity() {

    private lateinit var binding: ActivityDatosUserBinding
    private lateinit var auth: FirebaseAuth
    private val fileResult = 1

    private val db = FirebaseFirestore.getInstance()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityDatosUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        updateUI()

        binding.updateProfileAppCompatButton.setOnClickListener {

            val name = binding.nameEditText.text.toString()
            val lastname = binding.lastNameEditText.text.toString()
            val number = binding.numberEditText.text.toString()
            val direction = binding.directionEditText.text.toString()

            updateProfile(name,lastname,number,direction)

            db.collection("users").document(binding.emailTextView.text.toString()).set(
                hashMapOf(
                    "nombre" to binding.nameEditText.text.toString(),
                    "apellido" to binding.lastNameEditText.text.toString(),
                    "numero" to binding.numberEditText.text.toString(),
                    "direccion" to binding.directionEditText.text.toString(),
                    "guid" to FirebaseAuth.getInstance().currentUser!!.uid,
                    "tipo" to "usuario"
                ))

            home()
        }

        binding.profileImageView.setOnClickListener {
            fileManager()
        }

        binding.signOutImageView.setOnClickListener {
            signOut()
        }
    }

    private  fun updateProfile (name : String, lastname: String, number: String, direction: String) {

        val user = auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Se realizaron los cambios correctamente.",
                        Toast.LENGTH_SHORT).show()
                    updateUI()
                }
            }
    }

    private fun fileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, fileResult)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult) {
            if (resultCode == RESULT_OK && data != null) {
                val uri = data.data

                uri?.let { imageUpload(it) }

            }
        }
    }

    private fun imageUpload(mUri: Uri) {

        val user = auth.currentUser
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("Users")
        val fileName: StorageReference = folder.child("img"+user!!.uid)

        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { uri ->

                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse(uri.toString())
                }

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Se realizaron los cambios correctamente.",
                                Toast.LENGTH_SHORT).show()
                            updateUI()
                        }
                    }
            }
        }.addOnFailureListener {
            Log.i("TAG", "file upload error")
        }
    }



    private  fun updateUI () {
        val user = auth.currentUser

        if (user != null){
            binding.emailTextView.text = user.email

            Glide
                .with(this)
                .load(user.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.profileImageView)
            Glide
                .with(this)
                .load(user.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.bgProfileImageView)
        }

    }

    private fun home(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private  fun signOut(){
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        this.startActivity(intent)
    }

    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location : Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Null location", Toast.LENGTH_SHORT).show()
                    } else {
                        val addresses: List<Address>
                        val geocoder: Geocoder = Geocoder(this, Locale.getDefault())

                        addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )
                        val address: String =
                            addresses[0].getAddressLine(0)
                        binding.directionEditText.setText(address)
                    }

                }
            } else {
                Toast.makeText(this, "Active la ubicación", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // Pedir permisos
            requestPermission()
        }


    }
    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION)

    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}