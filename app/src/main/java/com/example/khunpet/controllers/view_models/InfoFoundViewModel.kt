package com.example.khunpet.controllers.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.PublicationFound
import com.example.khunpet.model.Usuario
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore

class InfoFoundViewModel : ViewModel() {

    val publication : MutableLiveData<PublicationFound> by lazy {
        MutableLiveData<PublicationFound>()
    }

    val user : MutableLiveData<Usuario> by lazy {
        MutableLiveData<Usuario>()
    }

    init {
        user.postValue(AppDatabase.getUsuarioConectado())
    }


    fun deletePublication() {

        FirebaseFirestore.getInstance().collection("encontrado").document(publication.value!!.documentId)
            .delete()
            .addOnSuccessListener {
                AppDatabase.getStorageReference().getReference("found/${publication.value!!.foto}")
                    .delete()
                    .addOnSuccessListener { Log.d("Delete", "DocumentSnapshot and Image successfully deleted!") }
                    .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
            }
            .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
    }

}