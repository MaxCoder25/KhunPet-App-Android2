package com.example.khunpet.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.khunpet.R
import com.example.khunpet.controllers.adapters.PublicationAdapter
import com.example.khunpet.controllers.view_models.LostAndFoundViewModel
import com.example.khunpet.databinding.FragmentLostAndFoundBinding
import com.example.khunpet.model.Publication
import com.example.khunpet.ui.activities.InfoActivity
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor


class LostAndFoundFragment : Fragment() {

    private val viewModel: LostAndFoundViewModel by viewModels()
    private var _binding: FragmentLostAndFoundBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickImg: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLostAndFoundBinding.inflate(inflater, container, false)
        pickImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val uri = it.data?.data!!
                val bitmap = uriToBitmap(uri, requireContext())
                val newBitmap = resizePhoto(bitmap)
                val newUri = bitmapToUri(newBitmap, requireContext())
                viewModel.imageUri.postValue(newUri)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.customCheck.isChecked = true

        binding.uploadImageButton.setOnClickListener {
            ImagePicker.with(requireActivity())
                .provider(ImageProvider.BOTH)
                .crop(224f,224f)
                .createIntentFromDialog { pickImg.launch(it) }
        }

        binding.requestButton.setOnClickListener {
            uploadImage()
        }

        viewModel.imageUri.observe(viewLifecycleOwner) {
            loadWithPicasso(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { bool ->
            if (bool) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.customCheck.setOnClickListener {
            binding.vggCheck.isChecked = false
            binding.disCheck.isChecked = false
            uncheckCheckboxes(binding.customCheck.isChecked, binding.vggCheck.isChecked, binding.disCheck.isChecked)
        }
        binding.vggCheck.setOnClickListener {
            binding.customCheck.isChecked = false
            binding.disCheck.isChecked = false
            uncheckCheckboxes(binding.customCheck.isChecked, binding.vggCheck.isChecked, binding.disCheck.isChecked)
        }
        binding.disCheck.setOnClickListener {
            binding.vggCheck.isChecked = false
            binding.customCheck.isChecked = false
            uncheckCheckboxes(binding.customCheck.isChecked, binding.vggCheck.isChecked, binding.disCheck.isChecked)
        }

        viewModel.model.observe(viewLifecycleOwner) {
            Log.d("Model", it.toString())
        }

        viewModel.retLiveData.observe(viewLifecycleOwner) {
            loadRecyclerView(it)
        }
    }

    private fun uncheckCheckboxes(custom : Boolean, vgg : Boolean, deep : Boolean) {
        if (custom) {
            viewModel.model.postValue(1)
        }
        else if (vgg) {
            viewModel.model.postValue(2)
        }
        else if (deep) {
            viewModel.model.postValue(3)
        }
    }

    fun resizePhoto(bitmap: Bitmap): Bitmap {
        val w = 224
        val h = 224
        return Bitmap.createScaledBitmap(bitmap, w, h, false)
    }

    fun uriToBitmap(uri: Uri, context: Context): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? = context.contentResolver
            .openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor?.fileDescriptor!!
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun bitmapToUri(inImage: Bitmap, inContext: Context): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando imagen...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        if (viewModel.imageUri.value != Uri.EMPTY) {
            viewModel.uploadImageToFirebaseStorage(requireContext())
            Toast.makeText(requireContext(), "Imagen subida con exito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Escoge una imagen primero", Toast.LENGTH_SHORT).show()
        }
        if (progressDialog.isShowing) progressDialog.dismiss()
    }

    private fun loadRecyclerView(items : List<Publication>) {
        binding.resultRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.resultRecyclerView.adapter = PublicationAdapter(items) { onClickPublication(it) }
    }


    private fun loadWithPicasso(uri: Uri) {
        if (uri != Uri.EMPTY) {
            Picasso.get()
                .load(uri)
                .fit().into(binding.uploadedImageView)
        } else  {
            Picasso.get()
                .load(R.drawable.place_holder)
                .fit().into(binding.uploadedImageView)
        }

    }

    private fun onClickPublication(publication: Publication) {
        val intent = Intent(requireActivity(), InfoActivity::class.java)
        val gson = Gson()
        val json = gson.toJson(publication)
        intent.putExtra("publication", json)
        startActivity(intent);
    }

}