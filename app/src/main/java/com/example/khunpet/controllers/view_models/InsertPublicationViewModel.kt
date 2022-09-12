package com.example.khunpet.controllers.view_models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.Publication
import com.example.khunpet.model.Usuario
import com.example.khunpet.utils.AppDatabase
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class InsertPublicationViewModel : ViewModel() {

    val imageUri : MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    val loading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
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
        loading.postValue(false)
        getUsuario()
        location.postValue(null)
    }

    fun getUsuario()  {
        val guid : String = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("guid", guid)
            .get()
            .addOnCompleteListener {
                if (it.result.documents.isNotEmpty()) {
                    var user = it.result.documents[0].toObject(Usuario::class.java)!!
                    usuario.postValue(user)
                }
            }
    }

    fun insertarPublicacion(publication: Publication) {
        loading.postValue(true)
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = AppDatabase.getStorageReference()

        storageReference.getReference("lost/$fileName.jpg").putFile(imageUri.value!!)
            .addOnSuccessListener {
                publication.fecha = now.toString()
                publication.foto = "$fileName.jpg"
                db.collection("publicacion")
                    .add(publication)
                    .addOnSuccessListener {
                        Log.d("Publication","Upload successfull")
                        loading.postValue(false)
                    }
                    .addOnFailureListener {
                        it.localizedMessage?.let { it1 -> Log.d("Publication", it1) }
                        loading.postValue(false)
                    }
            }
            .addOnFailureListener {
                it.localizedMessage?.let { it1 -> Log.d("Publication", it1) }
                loading.postValue(false)
            }
    }
}