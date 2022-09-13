package com.example.khunpet.controllers.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.Publication
import com.example.khunpet.model.Usuario
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore

class InfoViewModel : ViewModel() {

    val publication : MutableLiveData<Publication> by lazy {
        MutableLiveData<Publication>()
    }

    val user : MutableLiveData<Usuario> by lazy {
        MutableLiveData<Usuario>()
    }

    init {
        user.postValue(AppDatabase.getUsuarioConectado())
    }



    fun deletePublication() {

        FirebaseFirestore.getInstance().collection("publicacion").document(publication.value!!.documentId)
            .delete()
            .addOnSuccessListener {
                AppDatabase.getStorageReference().getReference("lost/${publication.value!!.foto}")
                    .delete()
                    .addOnSuccessListener { Log.d("Delete", "DocumentSnapshot and Image successfully deleted!") }
                    .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
            }
            .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
    }


}