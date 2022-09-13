package com.example.khunpet.controllers.view_models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.Publication
import com.example.khunpet.model.PublicationFound
import com.example.khunpet.model.Usuario
import com.example.khunpet.utils.AppDatabase
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class InsertFoundViewModel : ViewModel() {

    val imageUri : MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    val usuario : MutableLiveData<Usuario> by lazy {
        MutableLiveData<Usuario>()
    }

    val location : MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }

    private val db = FirebaseFirestore.getInstance()

    init {
        imageUri.postValue(Uri.EMPTY)
        getUsuario()
    }

    private fun getUsuario()  {
        usuario.postValue(AppDatabase.getUsuarioConectado())
    }

    fun insertarPublicacion(publication: PublicationFound) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = AppDatabase.getStorageReference()

        storageReference.getReference("found/$fileName.jpg").putFile(imageUri.value!!)
            .addOnSuccessListener {
                publication.fecha = now.toString()
                publication.foto = "$fileName.jpg"
                db.collection("encontrado")
                    .add(publication)
                    .addOnSuccessListener {
                        Log.d("PublicationFound","Upload successfull")
                    }
                    .addOnFailureListener {
                        it.localizedMessage?.let { it1 -> Log.d("Publication", it1) }
                    }
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.d("Publication", it1) }
            }
    }


}