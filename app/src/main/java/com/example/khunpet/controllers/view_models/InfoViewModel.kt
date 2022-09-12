package com.example.khunpet.controllers.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.Publication
import com.google.firebase.firestore.FirebaseFirestore

class InfoViewModel : ViewModel() {

    val publication : MutableLiveData<Publication> by lazy {
        MutableLiveData<Publication>()
    }



    fun deletePublication() {

        FirebaseFirestore.getInstance().collection("publicacion").document(publication.value!!.documentId)
            .delete()
            .addOnSuccessListener { Log.d("Delete", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
    }


}