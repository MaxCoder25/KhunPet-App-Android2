package com.example.khunpet.caso_uso

import android.util.Log
import com.example.khunpet.model.Publication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PublicationFetch {

    private lateinit var database: FirebaseFirestore

    suspend fun fetchPublication(): MutableList<Publication> = withContext(Dispatchers.Default) {
        database = FirebaseFirestore.getInstance()
        val ret: MutableList<Publication> = mutableListOf()
        val task = database.collectionGroup("publicacion").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(Publication::class.java)
                ret.add(publicacion)
            }
        }
        while (!task.isComplete) { }
        ret


    }
}