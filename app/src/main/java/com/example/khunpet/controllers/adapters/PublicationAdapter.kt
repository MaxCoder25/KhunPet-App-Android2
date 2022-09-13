package com.example.khunpet.controllers.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.khunpet.R
import com.example.khunpet.controllers.view_models.MainActivityViewModel
import com.example.khunpet.databinding.ItemPublicationListBinding
import com.example.khunpet.model.Publication
import com.example.khunpet.utils.AppDatabase
import com.squareup.picasso.Picasso

class PublicationAdapter(list: List<Publication>, val onClickItemSelected: (Publication) -> Unit) : RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder>()
{
    private var publicationList: MutableList<Publication> = list.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_publication_list, parent, false)
        return PublicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val item = publicationList[position]
        holder.render(item, position)
    }

    override fun getItemCount(): Int = publicationList.size

    inner class PublicationViewHolder(publicationView: View): RecyclerView.ViewHolder(publicationView) {

        var binding = ItemPublicationListBinding.bind(publicationView)
        var storage = AppDatabase.getStorageReference()
        fun render(item: Publication, index: Int) {

            val pubText : String = "Publicado: "+item.fecha
            val recText : String = "$: "+item.recompensa
            binding.fechaPublicacionText.text = pubText
            binding.tipoPublicacionText.text = item.tipo
            binding.recompensaPublicacionText.text = recText
            binding.contextoPublicationText.text = "Perdido"
            storage.getReference("lost").child(item.foto!!).downloadUrl
                .addOnSuccessListener {
                    Picasso.get()
                        .load(it.toString())
                        .fit()
                        .placeholder(R.drawable.place_holder)
                        .into(binding.imagePublicacion)
                }

            binding.itemPublication.setOnClickListener {
                onClickItemSelected(publicationList[index])
            }

        }


    }

}

