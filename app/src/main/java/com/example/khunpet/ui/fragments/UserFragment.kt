package com.example.khunpet.ui.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.khunpet.R
import com.example.khunpet.controllers.adapters.PublicationAdapter
import com.example.khunpet.controllers.view_models.UserViewModel
import com.example.khunpet.databinding.FragmentUserBinding
import com.example.khunpet.model.Publication
import com.example.khunpet.ui.activities.InfoActivity
import com.example.khunpet.utils.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.launch


class UserFragment : Fragment() {


    private val viewModel: UserViewModel by activityViewModels()
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.getItems(AppDatabase.getAuthInstance().currentUser!!.uid)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { bool ->
            if (bool) {
                binding.userLoadingBar.visibility = View.VISIBLE
            } else {
                binding.userLoadingBar.visibility = View.GONE
            }
        }

        viewModel.retLiveData.observe(viewLifecycleOwner) {
            loadRecyclerView(it)
        }

        viewModel.usuario.observe(viewLifecycleOwner) {
            binding.userCorreoTextView.text = AppDatabase.getAuthInstance().currentUser!!.email
            val nombre = viewModel.usuario.value!!.nombre + " " + viewModel.usuario.value!!.apellido
            binding.userNombreTextView.text = nombre
            binding.userTelefonoTextView.text = viewModel.usuario.value!!.numero
        }


    }

    private fun loadRecyclerView(items: List<Publication>) {
        binding.userPublicationRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.userPublicationRecyclerView.adapter =
            PublicationAdapter(items) { onClickPublication(it) }
    }

    private fun onClickPublication(publication: Publication) {
        val intent = Intent(requireActivity(), InfoActivity::class.java)
        val gson = Gson()
        val json = gson.toJson(publication)
        intent.putExtra("publication", json)
        startActivity(intent);
    }



}