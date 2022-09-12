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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class LostAndFoundViewModel : ViewModel() {

   // private val client = OkHttpClient()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val storageReference = AppDatabase.getStorageReference()

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


    fun uploadImageToFirebaseStorage() {

       // retLiveData.postValue(listOf())


        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        loading.postValue(true)
        storageReference.getReference("temp/$fileName.jpg").putFile(imageUri.value!!)


            .addOnSuccessListener {


                makeRequest(fileName)


            }
            .addOnFailureListener {
                imageUri.postValue(Uri.EMPTY)
            }

    }

    private fun makeRequest(fileName: String) {

        val request = Request.Builder()
            //.url("http://127.0.0.1:5000/$type/$fileName.jpg")
            .url("https://khunpet-autoencoder.herokuapp.com/custom/$fileName.jpg")
            .build()

        val type =  "custom"
/*
        when(model.value) {
            1 -> {
                val type =  "custom"
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/custom/$fileName.jpg")
                    //.url("https://khunpet-autoencoder.herokuapp.com/custom/$fileName.jpg")
                    .build()
                Log.d("Response:", request.toString())
            }
            2 -> {
                val type =    "vgg16"

                val request = Request.Builder()
                    //.url("http://127.0.0.1:5000/$type/$fileName.jpg")
                    .url("http://10.0.2.2:5000/vgg16/$fileName.jpg")
                    .build()
                Log.d("Response:", request.toString())
            }
            3 -> {
                val type =   "deepimagesearch"

                val request = Request.Builder()
                    //.url("http://127.0.0.1:5000/$type/$fileName.jpg")
                    .url("http://10.0.2.2:5000/deepimagesearch/$fileName.jpg")
                    .build()
                Log.d("Response:", request.toString())
            }
            else ->
             {
                 val type =    "custom"

                val request = Request.Builder()
                //.url("http://127.0.0.1:5000/$type/$fileName.jpg")
                .url("https://khunpet-autoencoder.herokuapp.com/custom/$fileName.jpg")
                .build()
                 Log.d("Response:", request.toString())

                   }
        }
*/





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

                    var listaDeepImageSearch = DeepImageSearchResponse()

                        lista = gson.fromJson(response.body!!.string(), object : TypeToken<MutableList<FlaskResponse?>?>() {}.type)
                        viewModelScope.launch {
                            getItems(lista)

                    }

                    Log.d("Response",lista.toString())
                  //  Log.d("Response",listaDeepImageSearch.toString())
                    loading.postValue(false)


                }
            }
        })
    }

    suspend fun getItems(lista : MutableList<FlaskResponse>) {
        val ret = PublicationFetch().fetchSimilarPets(lista)
        retLiveData.value = ret
        Log.d("Firebasefetch",ret.toString())
    }

    suspend fun getItemsDIS(listaDeepImageSearch : DeepImageSearchResponse) {
        val ret = PublicationFetch().fetchSimilarPetsDIS(listaDeepImageSearch)
        retLiveData.value = ret
        Log.d("Firebasefetch",ret.toString())
    }





}