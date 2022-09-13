package com.example.khunpet.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.khunpet.controllers.adapters.PublicationAdapter
import com.example.khunpet.controllers.adapters.PublicationAdapterRefug
import com.example.khunpet.controllers.adapters.PublicationFoundAdapter
import com.example.khunpet.controllers.view_models.FoundListViewModel
import com.example.khunpet.databinding.FragmentFoundListBinding
import com.example.khunpet.model.Publication
import com.example.khunpet.model.PublicationFound
import com.example.khunpet.ui.activities.InfoActivity
import com.example.khunpet.ui.activities.InfoFoundActivity
import com.google.gson.Gson

class FoundListFragment : Fragment() {


    private val viewModel: FoundListViewModel by activityViewModels()
    private var _binding: FragmentFoundListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoundListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) { bool ->
            if (bool) {
                binding.foundProgressBar.visibility = View.VISIBLE
            } else {
                binding.foundProgressBar.visibility = View.GONE
            }
        }

        viewModel.retLiveData.observe(viewLifecycleOwner) {
            loadRecyclerView(it)
        }

    }

    private fun loadRecyclerView(items: List<PublicationFound>) {
        binding.foundReciclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.foundReciclerView.adapter =
            PublicationFoundAdapter(items) { onClickPublication(it) }
    }

    private fun onClickPublication(publication: PublicationFound) {
        val intent = Intent(requireActivity(), InfoFoundActivity::class.java)
        val gson = Gson()
        val json = gson.toJson(publication)
        intent.putExtra("publication", json)
        startActivity(intent)
    }


}