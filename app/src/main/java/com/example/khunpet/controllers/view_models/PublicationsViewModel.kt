package com.example.khunpet.controllers.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.khunpet.caso_uso.PublicationFetch
import com.example.khunpet.model.Publication
import kotlinx.coroutines.launch

class PublicationsViewModel : ViewModel() {

    var retLiveData = MutableLiveData<List<Publication>>()

    var isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }


    init {
        viewModelScope.launch {
            getItems()
        }
    }

    suspend fun getItems() {
        isLoading.postValue(true)
        val ret = PublicationFetch().fetchPublication()
        retLiveData.postValue(ret)
        isLoading.postValue(false)
    }

}