package com.example.khunpet.controllers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.khunpet.R
import com.example.khunpet.databinding.ItemPublicationRefugBinding
import com.example.khunpet.model.AdoptionPublication
import com.example.khunpet.utils.AppDatabase
import com.squareup.picasso.Picasso

class PublicationAdapterRefug(list: List<AdoptionPublication>, val onClickItemSelected: (AdoptionPublication) -> Unit) : RecyclerView.Adapter<PublicationAdapterRefug.PublicationViewHolder>()
{
    private var publicationList: MutableList<AdoptionPublication> = list.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_publication_refug, parent, false)
        return PublicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val item = publicationList[position]
        holder.render(item, position)
    }

    override fun getItemCount(): Int = publicationList.size

    inner class PublicationViewHolder(publicationView: View): RecyclerView.ViewHolder(publicationView) {

        var binding = ItemPublicationRefugBinding.bind(publicationView)
        var storage = AppDatabase.getStorageReference()

        fun render(item: AdoptionPublication, index: Int) {

            binding.nameMascota.text= item.Name

            binding.fechaPublicacionText.text = "Fecha: "+item.publicationDate_Mascota

            storage.getReference("lost/images").child(item.ImageUrl!!).downloadUrl
                .addOnSuccessListener {
                    Picasso.get()
                        .load(it.toString())
                        .fit()
                        .placeholder(R.drawable.place_holder)
                        .into(binding.imagePublicacion)
                }

            binding.itemPublicationRefug.setOnClickListener {
                onClickItemSelected(publicationList[index])
            }

        }


    }

}

