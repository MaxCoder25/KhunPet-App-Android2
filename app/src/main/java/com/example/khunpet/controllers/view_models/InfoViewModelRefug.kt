package com.example.khunpet.controllers.view_models

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.example.khunpet.databinding.ActivityInfoBinding
import com.example.khunpet.databinding.ActivityInfoRefugBinding
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.utils.AppDatabase
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class InfoViewModelRefug : ViewModel() {
    //private val client = OkHttpClient()




    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()








    val storageReference = AppDatabase.getStorageReference()

    val publication : MutableLiveData<AdoptionPublication> by lazy {
        MutableLiveData<AdoptionPublication>()
    }

    var retLiveData = MutableLiveData<List<AdoptionPublication>>()

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


 /*  fun uploadImageToFirebaseStorage() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        loading.postValue(true)
        storageReference.getReference("lost/images/$fileName.jpg").putFile(imageUri.value!!)
            .addOnSuccessListener {
                makeRequest(fileName)
            }
            .addOnFailureListener {
                imageUri.postValue(Uri.EMPTY)
            }
    }*/

     fun makeRequest(petID: String){
        // var petID="000aa306a"
         loading.postValue(true)

         val request = Request.Builder()
            //.url("http://heroku/predict/$fileName.jpg")
         //127.0.0.1 no funciona es localhost compu no android emulator

            .url("http://10.0.2.2:5000/predict/$petID")

            .build()
       //  client.newCall(request).execute()

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

                    //val gson = Gson()
                    //var adoptionTimeResponse : MutableList<FlaskResponseRefug> = mutableListOf()

                      //  adoptionTimeResponse = gson.fromJson(response.body!!.string(), object : TypeToken<MutableList<FlaskResponseRefug?>?>() {}.type)




                    var adoptionTimeResponse = response.body?.string()

                    var adoptionTimeResponseDigit = (adoptionTimeResponse?.get(18)).toString()

                   //  var adoptionTimeResponse: FlaskResponseRefug? = gson.fromJson(response.body!!.string(), FlaskResponseRefug::class.java)

                    viewModelScope.launch {

                        if (adoptionTimeResponse != null) {

                           saveSharedPreferencepetIDResponse(adoptionTimeResponseDigit)
                         //   saveSharedPreferencepetIDResponse(adoptionTimeResponse[0].AdoptionPrediction)

                        }


                   }

                    if (adoptionTimeResponse != null) {
                        //Log.d("Response",adoptionTimeResponse[0].AdoptionPrediction)
                        Log.d("Response",adoptionTimeResponse)
                        Log.d("Responsedigit",adoptionTimeResponseDigit)

                    }

                      loading.postValue(false)


                }


            }

        })

    }

    fun saveSharedPreferencepetIDResponse(petID: String) {
        var editorSP = AppDatabase.getShareDB().edit()
        editorSP.putString ("petIDResponse", petID )
        editorSP.commit()
    }



    fun getStringSharedPreference_petAUX(): String? {
        var editorSP = AppDatabase.getShareDB()
        var petID = editorSP.getString("petAUX", "null")

        return petID

    }





}