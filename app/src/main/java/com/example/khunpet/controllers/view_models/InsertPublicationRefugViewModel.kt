package com.example.khunpet.controllers.view_models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class InsertPublicationRefugViewModel : ViewModel() {


    val imageUri : MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    val jsonUri : MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }


    val loading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private val db = FirebaseFirestore.getInstance()



    init {
        imageUri.postValue(Uri.EMPTY)
        jsonUri.postValue(Uri.EMPTY)
        loading.postValue(false)
    }


    fun publish(publication: AdoptionPublication, PetID: String) {
        val pattern = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val now: String = simpleDateFormat.format(Date())

        publication.publicationDate_Mascota = now
        publication.ImageUrl = uploadImageToFirebaseStorage(PetID)

        //uploadJsonToFirebaseStorage(PetID)


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

     fun uploadJsonToFirebaseStorage() {

         val PetID = getSharedPreferencePetIDVISION()

         val storageReference = AppDatabase.getStorageReference()


         var file = Uri.fromFile(File("/storage/1DFA-2608/MyIdea/" + PetID + "-1.json"))
         val riversRef = storageReference.getReference().child("lost/imageJson/$PetID-1.json")
         var uploadTask = riversRef.putFile(file)

         uploadTask.addOnFailureListener {
             // Handle unsuccessful uploads
         }.addOnSuccessListener { taskSnapshot ->
             // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
             Log.d("Upload","Upload successfull json file")

         }


         /*
        var task = storageReference.getReference("lost/imageJson/$PetID-1.json").putFile(jsonUri.value!!)
            .addOnSuccessListener {
                Log.d("Upload","Upload successfull json file")
            }
            .addOnFailureListener {
                imageUri.postValue(Uri.EMPTY)
            }

        while (!task.isComplete){}
        return "$PetID-1.json"*/



    }

    fun  getSharedPreferencePetIDVISION(): String? {
        var editorSP = AppDatabase.getShareDB()
        var petIDVISION = editorSP.getString("petIDVISION", "a2sf425b")

        return petIDVISION

    }



}