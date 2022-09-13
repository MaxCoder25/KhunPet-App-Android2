package com.example.khunpet.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.InsertFoundViewModel
import com.example.khunpet.controllers.view_models.MainActivityRefugViewModel
import com.example.khunpet.controllers.view_models.MapViewModel
import com.example.khunpet.databinding.FragmentInsertFoundBinding
import com.example.khunpet.model.Publication
import com.example.khunpet.model.PublicationFound
import com.example.khunpet.utils.AppDatabase
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern

class InsertFoundFragment : Fragment() {


    private val viewModel: InsertFoundViewModel by activityViewModels()
    private val activityViewModel : MainActivityRefugViewModel by activityViewModels()
    private var _binding: FragmentInsertFoundBinding? = null
    private val binding get() = _binding!!

    private val regex = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*\$"
    private var pattern: Pattern = Pattern.compile(regex)

    private lateinit var selectImg: ActivityResultLauncher<Intent>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsertFoundBinding.inflate(inflater, container, false)
        selectImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                viewModel.imageUri.postValue(uri)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())


        binding.imageButton.setOnClickListener {
            ImagePicker.with(requireActivity())
                .provider(ImageProvider.BOTH)
                .crop(224f,224f)
                .createIntentFromDialog { selectImg.launch(it) }
        }

        binding.publicarButton.setOnClickListener {
            buildPublication()
        }

        viewModel.imageUri.observe(viewLifecycleOwner) {
            loadWithPicasso(it)
        }


        binding.ubicacionActualButton.setOnClickListener {
            getCurrentLocation()
        }

        binding.abrirMapaButton.setOnClickListener {
            goToMap()
        }

        viewModel.usuario.observe(viewLifecycleOwner) {
            binding.telefonoEditText.setText(it.numero)
        }

        viewModel.location.observe(viewLifecycleOwner) {
            if (it != null) {
                val addresses: List<Address>
                val geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    it.latitude,
                    it.longitude, 1
                )
                val address: String =
                    addresses[0].getAddressLine(0)
                binding.encontradoEditText.setText(address)
            }
        }
    }

    private fun buildPublication() {

        if (viewModel.imageUri.value == Uri.EMPTY) {
            Toast.makeText(requireContext(), "Es necesario una imagen", Toast.LENGTH_SHORT).show()
            return
        }
        if (!pattern.matcher(binding.telefonoEditText.text.toString()).find()) {
            Toast.makeText(requireContext(), "Es necesario un telefono de contacto válido", Toast.LENGTH_SHORT).show()
            return
        }

        val tipo = when(binding.tipoRadioGroup.checkedRadioButtonId) {
            R.id.perroRadioButton -> {
                "Perro"
            }
            R.id.gatoRadioButton -> {
                "Gato"
            } else -> "Perro"
        }

        val condicion = when(binding.condicionRadioGroup.checkedRadioButtonId) {
            R.id.saludableRadioButton -> {
                "Saludable"
            }
            R.id.graveRadioButton -> {
                "Grave"
            }
            R.id.criticoRadioButton -> {
                "Crítico"
            }
            else -> "Saludable"
        }

        val publication = PublicationFound()

        val locacion = binding.encontradoEditText.text.toString()
        val telefono = binding.telefonoEditText.text.toString()
        val descripcion = binding.comentarioEditText.text.toString()

        publication.locacion = locacion
        publication.telefono = telefono
        publication.descripcion = descripcion
        publication.condicion = condicion
        publication.tipo = tipo
        publication.user = viewModel.usuario.value!!.guid
        publication.contexto = "Encontrado"
        publication.nombre = AppDatabase.getUsuarioConectado().nombre


        lifecycleScope.launch {
            viewModel.insertarPublicacion(publication)
            Toast.makeText(requireContext(), "Publicación exitosa", Toast.LENGTH_SHORT).show()
        }




    }

    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location : Location? = task.result
                    if (location == null) {
                        Toast.makeText(requireContext(), "Null location", Toast.LENGTH_SHORT).show()
                    } else {
                        val addresses: List<Address>
                        val geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())

                        addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude, 1
                        )
                        val address: String =
                            addresses[0].getAddressLine(0)
                        binding.encontradoEditText.setText(address)
                    }

                }
            } else {
                Toast.makeText(requireContext(), "Active la ubicación", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // Pedir permisos
            requestPermission()
        }
    }

    private fun goToMap() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location : Location? = task.result
                    if (location == null) {
                        Toast.makeText(requireContext(), "Null location", Toast.LENGTH_SHORT).show()
                    } else {
                        /*
                        val intent = Intent(requireContext(), DisplayMapActivity::class.java)
                        intent.putExtra("LONGITUD", location.longitude)
                        intent.putExtra("LATIDUD", location.latitude)
                        startActivity(intent)
                        */
                        val mapviewModel: MapViewModel by activityViewModels()
                        mapviewModel.location.postValue(LatLng(location.latitude, location.longitude))
                        activityViewModel.changeFragment(10)

                    }

                }
            } else {
                Toast.makeText(requireContext(), "Active la ubicación", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // Pedir permisos
            requestPermission()
        }
    }



    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager : LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION)

    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permisos concedidos", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadWithPicasso(uri: Uri) {
        if (uri != Uri.EMPTY) {
            Picasso.get()
                .load(uri)
                .fit().into(binding.uploadImageView)
        } else  {
            Picasso.get()
                .load(R.drawable.place_holder)
                .fit().into(binding.uploadImageView)
        }

    }

}