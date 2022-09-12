package com.example.khunpet.controllers.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khunpet.caso_uso.PublicationFetch
import com.example.khunpet.model.Publication
import com.example.khunpet.model.Usuario
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {


    val usuario : MutableLiveData<Usuario> by lazy {
        MutableLiveData<Usuario>()
    }
    var retLiveData = MutableLiveData<List<Publication>>()

    var isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }

    private fun getUsuario()  {
        val guid : String = AppDatabase.getAuthInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("guid", guid)
            .get()
            .addOnCompleteListener {
                if (it.result.documents.isNotEmpty()) {
                    usuario.postValue(it.result.documents[0].toObject(Usuario::class.java)!!)
                }
            }
    }

    init {
        getUsuario()
    }

    suspend fun getItems(guid:String) {
        isLoading.postValue(true)
        val ret = PublicationFetch().fetchPublicationUser(guid)
        retLiveData.postValue(ret)
        isLoading.postValue(false)
    }


}