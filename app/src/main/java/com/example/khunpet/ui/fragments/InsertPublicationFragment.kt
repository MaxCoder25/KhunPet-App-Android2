package com.example.khunpet.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.InsertPublicationViewModel
import com.example.khunpet.databinding.FragmentInsertPublicationBinding
import com.example.khunpet.model.Publication
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.squareup.picasso.Picasso
import java.util.regex.Pattern


class InsertPublicationFragment : Fragment() {


    private val viewModel : InsertPublicationViewModel by viewModels()
    private var _binding: FragmentInsertPublicationBinding? = null
    private val binding get() = _binding!!

    private val regex = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*\$"
    private var pattern: Pattern = Pattern.compile(regex)

    private lateinit var selectImg: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsertPublicationBinding.inflate(inflater, container, false)
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

        viewModel.loading.observe(viewLifecycleOwner) { bool ->
            if (bool) {
                binding.loadingPublicacion.visibility = View.VISIBLE
            } else {
                binding.loadingPublicacion.visibility = View.GONE
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

        val locacion = binding.encontradoEditText.text.toString()
        val telefono = binding.telefonoEditText.text.toString()
        val descripcion = binding.comentarioEditText.text.toString()

        val tipo = "Perdido y Encontrado"

        val condicion = when(binding.condicionRadioGroup.checkedRadioButtonId) {
            R.id.saludableRadioButton -> {
                "saludable"
            }
            R.id.graveRadioButton -> {
                "grave"
            }
            R.id.criticoRadioButton -> {
                "critico"
            }
            else -> "saludable"
        }

        val publication = Publication(locacion,telefono,descripcion,tipo,condicion)

        viewModel.publish(publication)

        Toast.makeText(requireContext(), "Publicación exitosa", Toast.LENGTH_SHORT).show()

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