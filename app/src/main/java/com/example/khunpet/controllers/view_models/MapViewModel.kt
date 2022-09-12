package com.example.khunpet.controllers.view_models

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {

    val location : MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }

    init {
        location.postValue(LatLng(-0.180653,-78.467834))
    }
}