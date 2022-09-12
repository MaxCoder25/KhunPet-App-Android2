package com.example.khunpet.caso_uso

import android.util.Log
import com.example.khunpet.model.DeepImageSearchResponse
import com.example.khunpet.model.FlaskResponse
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

    suspend fun fetchPublicationUser(guid : String): MutableList<Publication> = withContext(Dispatchers.Default) {
        database = FirebaseFirestore.getInstance()
        val ret: MutableList<Publication> = mutableListOf()
        val task = database.collection("publicacion").whereEqualTo("user",guid).get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(Publication::class.java)
                ret.add(publicacion)
            }
        }
        while (!task.isComplete) { }
        Log.d("fetchuser",ret.toString())
        ret

    }

    suspend fun fetchSimilarPets(lista : MutableList<FlaskResponse>): MutableList<Publication> = withContext(Dispatchers.Default) {
        database = FirebaseFirestore.getInstance()
        val photos : MutableList<String> = lista.map { p -> p.image }.toMutableList()
        val ret: MutableList<Publication> = mutableListOf()
        val task = database.collectionGroup("publicacion").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(Publication::class.java)
                if (photos.contains(publicacion.foto)) {
                    ret.add(publicacion)
                }

            }
        }
        while (!task.isComplete) { }
        Log.d("fetchSimilarPets",ret.toString())
        ret
    }


    suspend fun fetchSimilarPetsDIS(listaDeepImageSearch : DeepImageSearchResponse): MutableList<Publication> = withContext(Dispatchers.Default) {
        database = FirebaseFirestore.getInstance()
        val ret: MutableList<Publication> = mutableListOf()
        val task = database.collectionGroup("publicacion").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(Publication::class.java)
                if (listaDeepImageSearch.contains(publicacion.foto)) {
                    ret.add(publicacion)
                }
            }

        }
        while (!task.isComplete) { }
        Log.d("fetchSimilarPetsDIS",ret.toString())
        ret
    }


}