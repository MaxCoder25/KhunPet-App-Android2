package com.example.khunpet.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.InsertFoundViewModel
import com.example.khunpet.controllers.view_models.MainActivityRefugViewModel
import com.example.khunpet.controllers.view_models.MapViewModel
import com.example.khunpet.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapFragmentFound : Fragment(), OnMapReadyCallback {

    private val viewModel: MapViewModel by activityViewModels()
    private val activityViewModel : MainActivityRefugViewModel by activityViewModels()
    private val insertViewModel : InsertFoundViewModel by activityViewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.salirButton.setOnClickListener {
            activityViewModel.changeFragment(R.id.publications_found_insertar_button)
        }

        binding.tomarLocacionButton.setOnClickListener {


            insertViewModel.location.postValue(mMap.cameraPosition.target)

            activityViewModel.changeFragment(R.id.publications_found_insertar_button)

        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(viewModel.location.value!!, 15f), null
        )
    }


}