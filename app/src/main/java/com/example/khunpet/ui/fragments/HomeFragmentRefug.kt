package com.example.khunpet.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.khunpet.controllers.adapters.PublicationAdapterRefug
import com.example.khunpet.controllers.view_models.MainActivityRefugViewModel
import com.example.khunpet.controllers.view_models.PublicationsRefugViewModel
import com.example.khunpet.databinding.FragmentHomeRefugBinding
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.ui.activities.InfoActivityRefug
import com.google.gson.Gson
import kotlinx.coroutines.launch

class HomeFragmentRefug : Fragment() {

    private val viewModel: PublicationsRefugViewModel by viewModels()
    private val activityViewModel: MainActivityRefugViewModel by activityViewModels()
    private var _binding: FragmentHomeRefugBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeRefugBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.getItems()
        }


        viewModel.isLoading.observe(viewLifecycleOwner) { bool ->
            if (bool) {
                binding.publicationProgressBar.visibility = View.VISIBLE
            } else {
                binding.publicationProgressBar.visibility = View.GONE
            }
        }

        viewModel.retLiveData.observe(viewLifecycleOwner) {
            loadRecyclerView(it)
        }

    }

    private fun loadRecyclerView(items: List<AdoptionPublication>) {
        binding.publicacionReciclerView.layoutManager =
            GridLayoutManager(binding.publicacionReciclerView.context, 2)
        binding.publicacionReciclerView.adapter =
            PublicationAdapterRefug(items) { onClickPublication(it) }
    }

    private fun onClickPublication(AdoptionPublication: AdoptionPublication) {
        val intent = Intent(requireActivity(), InfoActivityRefug::class.java)
        val gson = Gson()
        val json = gson.toJson(AdoptionPublication)
        intent.putExtra("publication", json)
        startActivity(intent);
    }
}