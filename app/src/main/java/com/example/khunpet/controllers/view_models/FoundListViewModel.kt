package com.example.khunpet.controllers.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khunpet.caso_uso.PublicationFetch
import com.example.khunpet.model.Publication
import com.example.khunpet.model.PublicationFound
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoundListViewModel : ViewModel() {

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
                ret.add(publicacion)
            }
            retLiveData.postValue(ret)
        }
    }
}