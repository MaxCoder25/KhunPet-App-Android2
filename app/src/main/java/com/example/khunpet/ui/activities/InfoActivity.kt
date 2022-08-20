package com.example.khunpet.ui.activities


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.InfoViewModel
import com.example.khunpet.databinding.ActivityInfoBinding
import com.example.khunpet.model.Publication
import com.example.khunpet.utils.AppDatabase
import com.google.gson.Gson
import com.squareup.picasso.Picasso


class InfoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityInfoBinding
    val viewModel : InfoViewModel by viewModels()
    var storage = AppDatabase.getStorageReference()
    private lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val pub : String? = intent.getStringExtra("publication")
        val gson = Gson()
        viewModel.publication.postValue(gson.fromJson(pub,Publication::class.java))



        viewModel.publication.observe(this) {
            loadInformation(it)
        }

        binding.wspButton.setOnClickListener {
            val numberLocal : String? = viewModel.publication.value!!.telefono
            val numberInternational : String = "593" + numberLocal!!.subSequence(1, numberLocal.length)
            phoneNumber = numberInternational
            openWhatsApp(numberInternational)
        }

        binding.callButton.setOnClickListener {
            val numberLocal : String? = viewModel.publication.value!!.telefono
            val numberInternational : String = "593" + numberLocal!!.subSequence(1, numberLocal.length)
            phoneNumber = numberInternational
            onCall(numberInternational)
        }
    }


    private fun loadInformation(publication: Publication) {
        storage.getReference("lost").child(publication.foto!!).downloadUrl
            .addOnSuccessListener {
                binding.infoLocacion.text = publication.locacion
                binding.infoCondicion.text = publication.condicion
                binding.infoComentario.text = publication.descripcion
                binding.infoFecha.text = publication.fecha
                binding.infoContacto.text = publication.telefono
                Picasso.get()
                    .load(it.toString())
                    .fit()
                    .placeholder(R.drawable.place_holder)
                    .into(binding.publicacionImv)
            }
    }

    private fun openWhatsApp(num: String) {
        val isAppInstalled = appInstalledOrNot("com.whatsapp")
        if (isAppInstalled) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$num"))
            startActivity(intent)
        } else {
            Toast.makeText(this,"WhatsApp no instalado",Toast.LENGTH_SHORT).show()
        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = this.packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun onCall(number: String) {
        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CALL_PHONE),
                123
            )
        } else {
            startActivity(Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:$number")))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            123 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCall(phoneNumber)
            } else {
                Log.d("TAG", "Call Permission Not Granted")
            }
            else -> {}
        }
    }
}