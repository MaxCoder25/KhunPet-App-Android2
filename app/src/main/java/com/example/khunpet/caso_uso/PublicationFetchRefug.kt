package com.example.khunpet.caso_uso

import android.util.Log
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.model.DeepImageSearchResponse
import com.example.khunpet.model.FlaskResponse
import com.example.khunpet.model.Usuario
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception


class PublicationFetchRefug {

    private lateinit var database: FirebaseFirestore

    suspend fun fetchPublication(): MutableList<AdoptionPublication> = withContext(Dispatchers.Default) {

        val currentuser = FirebaseAuth.getInstance().currentUser!!.email


        database = FirebaseFirestore.getInstance()
        val ret1: MutableList<Usuario> = mutableListOf()

        val task1 = database.collectionGroup("users").get().addOnSuccessListener { resultado->

            for(documento in resultado){

                if(documento.toObject(Usuario::class.java).correo == currentuser ){
                    val usuario = documento.toObject(Usuario::class.java)

                    documento.toObject(Usuario::class.java).nombre?.let {
                        saveSharedPreferenceUserName(
                            it
                        )
                    }


                    documento.toObject(Usuario::class.java).numero?.let {
                        saveSharedPreferenceUserNumber(
                            it
                        )
                    }



                    ret1.add(usuario)
                }

            }
        }
        while (!task1.isComplete) {

        }

       // ret1




        database = FirebaseFirestore.getInstance()
        val ret: MutableList<AdoptionPublication> = mutableListOf()

        val task = database.collectionGroup("mascotaEnAdopcion").get().addOnSuccessListener { resultado->

            try {
                for(documento in resultado){

                    if(documento.toObject(AdoptionPublication::class.java ).refugio==ret1[0].nombre ){
                        val publicacion = documento.toObject(AdoptionPublication::class.java)
                        ret.add(publicacion)
                    }

                }
            }catch (e: Exception){}

        }
        while (!task.isComplete) {

        }
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

    fun saveSharedPreferenceUserNumber(UserNumber: String) {
        var editorSP = AppDatabase.getShareDB().edit()
        editorSP.putString ("UserNumber", UserNumber )
        editorSP.commit()
    }


    fun saveSharedPreferenceUserName(UserName: String) {
        var editorSP = AppDatabase.getShareDB().edit()
        editorSP.putString ("UserName", UserName )
        editorSP.commit()
    }









}