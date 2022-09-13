package com.example.khunpet.controllers.view_models

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Video
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khunpet.caso_uso.PublicationFetch
import com.example.khunpet.model.DeepImageSearchResponse
import com.example.khunpet.model.FlaskResponse
import com.example.khunpet.model.Publication
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class LostAndFoundViewModel : ViewModel() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val storageReference = AppDatabase.getStorageReference()

    var retLiveData = MutableLiveData<List<Publication>>()

    val imageUri : MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    val loading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val model : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    init {
        imageUri.postValue(Uri.EMPTY)
        loading.postValue(false)
        model.postValue(1)
    }


    fun uploadImageToFirebaseStorage(tipo: Int) {

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        loading.postValue(true)
        storageReference.getReference("temp/$fileName.jpg").putFile(imageUri.value!!)

            .addOnSuccessListener {
                makeRequest(fileName, tipo)
            }
            .addOnFailureListener {
                imageUri.postValue(Uri.EMPTY)
            }

    }

    private fun makeRequest(fileName: String, tipo: Int) {

        val request = Request.Builder()
            .url("http://192.168.100.144:5000/vgg16/$fileName.jpg/$tipo")
            .build()



        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Response:",e.stackTraceToString())
                loading.postValue(false)
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        Log.d("Response:","$name: $value")
                    }
                    val gson = Gson()
                    var lista : MutableList<FlaskResponse> = mutableListOf()

                    lista = gson.fromJson(response.body!!.string(), object : TypeToken<MutableList<FlaskResponse?>?>() {}.type)
                    fetchSimilarPets(lista)
                    Log.d("Response",lista.toString())
                    loading.postValue(false)
                    deletePublication(fileName)
                }
            }
        })
    }

    fun fetchSimilarPets(lista : MutableList<FlaskResponse>) {
        val database = FirebaseFirestore.getInstance()
        val photos : MutableList<String> = lista.map { p -> p.image }.toMutableList()
        val ret: MutableList<Publication> = mutableListOf()
        database.collectionGroup("publicacion").get().addOnSuccessListener { resultado->
            for(documento in resultado){
                val publicacion=documento.toObject(Publication::class.java)
                if (photos.contains(publicacion.foto)) {
                    ret.add(publicacion)
                }
            }
            retLiveData.postValue(ret)
        }
    }


    fun deletePublication(fileName: String) {
        AppDatabase.getStorageReference().getReference("temp/$fileName.jpg")
            .delete()
            .addOnSuccessListener { Log.d("Delete", "DocumentSnapshot and Image successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
    }




}