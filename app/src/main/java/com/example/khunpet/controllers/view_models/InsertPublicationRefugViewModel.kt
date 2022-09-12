package com.example.khunpet.controllers.view_models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class InsertPublicationRefugViewModel : ViewModel() {


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


    fun publish(publication: AdoptionPublication, PetID: String) {
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val now: String = simpleDateFormat.format(Date())

        publication.publicationDate_Mascota = now
        publication.ImageUrl = uploadImageToFirebaseStorage(PetID)



        db.collection("mascotaEnAdopcion")
            .document(PetID)
            .set(publication)
            //.add(publication)

            .addOnSuccessListener {
                Log.d("Upload","Upload successfull")
            }
            .addOnFailureListener {
                Log.d("Upload", it.localizedMessage)
            }

    }

    fun uploadImageToFirebaseStorage(PetID: String) : String {

        val fileName = PetID

        val storageReference = AppDatabase.getStorageReference()

        var task = storageReference.getReference("lost/images/$fileName-1.jpg").putFile(imageUri.value!!)
            .addOnSuccessListener {
                Log.d("Upload","Upload successfull")
            }
            .addOnFailureListener {
                imageUri.postValue(Uri.EMPTY)
            }

        while (!task.isComplete){}
        return "$fileName-1.jpg"
    }
}