package com.example.khunpet.controllers.view_models

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.Publication
import com.example.khunpet.utils.AppDatabase
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

    private val db = FirebaseFirestore.getInstance()



    init {
        imageUri.postValue(Uri.EMPTY)
        loading.postValue(false)
    }


    fun publish(publication: Publication) {
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val now: String = simpleDateFormat.format(Date())

        publication.fecha = now
        publication.foto = uploadImageToFirebaseStorage()

        db.collection("publicacion")
            .add(publication)
            .addOnSuccessListener {
                Log.d("Upload","Upload successfull")
            }
            .addOnFailureListener {
                Log.d("Upload", it.localizedMessage)
            }

    }


    fun uploadImageToFirebaseStorage() : String {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = AppDatabase.getStorageReference()

        var task = storageReference.getReference("lost/$fileName.jpg").putFile(imageUri.value!!)
            .addOnSuccessListener {
                Log.d("Upload","Upload successfull")
            }
            .addOnFailureListener {
                imageUri.postValue(Uri.EMPTY)
            }

        while (!task.isComplete){}
        return "$fileName.jpg"
    }
}