package com.example.khunpet.controllers.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.khunpet.model.Publication

class InfoViewModel : ViewModel() {

    val publication : MutableLiveData<Publication> by lazy {
        MutableLiveData<Publication>()
    }


}