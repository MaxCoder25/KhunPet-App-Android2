package com.example.khunpet.controllers.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.PublicationFound
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FoundListViewModelRefug : ViewModel() {

    var retLiveData = MutableLiveData<List<PublicationFound>>()

    var isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }

    init {
        fetchPublication()
    }

    fun fetchPublication() {
        val database = FirebaseFirestore.getInstance()
        val ret: MutableList<PublicationFound> = mutableListOf()
        database.collectionGroup("encontrado").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(PublicationFound::class.java)
                if (AppDatabase.getUsuarioConectado().guid == publicacion.user) {
                    ret.add(publicacion)
                }
            }
            retLiveData.postValue(ret)
        }
    }
}