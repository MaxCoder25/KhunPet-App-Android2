package com.example.khunpet.caso_uso

import android.util.Log
import com.example.khunpet.model.DeepImageSearchResponse
import com.example.khunpet.model.FlaskResponse
import com.example.khunpet.model.Publication
import com.example.khunpet.model.AdoptionPublication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PublicationFetchRefug {

    private lateinit var database: FirebaseFirestore

    suspend fun fetchPublication(): MutableList<AdoptionPublication> = withContext(Dispatchers.Default) {
        database = FirebaseFirestore.getInstance()
        val ret: MutableList<AdoptionPublication> = mutableListOf()
        val task = database.collectionGroup("mascotaEnAdopcion").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(AdoptionPublication::class.java)
                ret.add(publicacion)
            }
        }
        while (!task.isComplete) { }
        ret

    }

    suspend fun fetchSimilarPets(lista : MutableList<FlaskResponse>): MutableList<AdoptionPublication> = withContext(Dispatchers.Default) {
        database = FirebaseFirestore.getInstance()
        val photos : MutableList<String> = lista.map { p -> p.image }.toMutableList()
        val ret: MutableList<AdoptionPublication> = mutableListOf()
        val task = database.collectionGroup("publicacion").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(AdoptionPublication::class.java)
                if (photos.contains(publicacion.ImageUrl)) {
                    ret.add(publicacion)
                }

            }
        }
        while (!task.isComplete) { }
        Log.d("fetchSimilarPets",ret.toString())
        ret
    }


    suspend fun fetchSimilarPetsDIS(listaDeepImageSearch : DeepImageSearchResponse): MutableList<AdoptionPublication> = withContext(Dispatchers.Default) {
        database = FirebaseFirestore.getInstance()
        val ret: MutableList<AdoptionPublication> = mutableListOf()
        val task = database.collectionGroup("publicacion").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(AdoptionPublication::class.java)
                if (listaDeepImageSearch.contains(publicacion.ImageUrl)) {
                    ret.add(publicacion)
                }
            }

        }
        while (!task.isComplete) { }
        Log.d("fetchSimilarPetsDIS",ret.toString())
        ret
    }
}