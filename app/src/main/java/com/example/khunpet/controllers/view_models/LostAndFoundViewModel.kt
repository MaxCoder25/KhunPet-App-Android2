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


class LostAndFoundViewModel : ViewModel() {

    private val client = OkHttpClient()

    var retLiveData = MutableLiveData<List<Publication>>()

    var isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }

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


    fun uploadImageToFirebaseStorage(context: Context) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = AppDatabase.getStorageReference()

        storageReference.getReference("temp/$fileName.jpg").putFile(imageUri.value!!)
            .addOnSuccessListener {
                makeRequest(fileName)
            }
            .addOnFailureListener {
                imageUri.postValue(Uri.EMPTY)
            }

        loading.postValue(true)

    }

    private fun makeRequest(fileName: String) {
        val type = when(model.value) {
            1 -> {
                "custom"
            }
            2 -> {
                "vgg16"
            }
            3 -> {
                "deepimagesearch"
            }
            else -> "custom"
        }

        val request = Request.Builder()
            .url("http://192.168.100.143:5000/$type/$fileName.jpg")
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
                    var listaDeepImageSearch = DeepImageSearchResponse()
                    if (type != "deepimagesearch") {
                        lista = gson.fromJson(response.body!!.string(), object : TypeToken<MutableList<FlaskResponse?>?>() {}.type)
                        viewModelScope.launch {
                            getItems(lista)
                        }
                    } else {
                        listaDeepImageSearch = gson.fromJson(response.body!!.string(), DeepImageSearchResponse::class.java)
                        viewModelScope.launch {
                            getItemsDIS(listaDeepImageSearch)
                        }
                    }
                    Log.d("Response",lista.toString())
                    Log.d("Response",listaDeepImageSearch.toString())
                    loading.postValue(false)
                }
            }
        })
    }

    suspend fun getItems(lista : MutableList<FlaskResponse>) {
        val ret = PublicationFetch().fetchSimilarPets(lista)
        retLiveData.postValue(ret)
        isLoading.postValue(false)
    }

    suspend fun getItemsDIS(listaDeepImageSearch : DeepImageSearchResponse) {
        val ret = PublicationFetch().fetchSimilarPetsDIS(listaDeepImageSearch)
        retLiveData.postValue(ret)
        isLoading.postValue(false)
    }





}