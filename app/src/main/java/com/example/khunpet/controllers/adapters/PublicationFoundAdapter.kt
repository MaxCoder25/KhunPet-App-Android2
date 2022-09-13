package com.example.khunpet.controllers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.khunpet.R
import com.example.khunpet.databinding.ItemPublicationfoundListBinding
import com.example.khunpet.model.PublicationFound
import com.example.khunpet.utils.AppDatabase
import com.squareup.picasso.Picasso

class PublicationFoundAdapter(list: List<PublicationFound>, val onClickItemSelected: (PublicationFound) -> Unit) : RecyclerView.Adapter<PublicationFoundAdapter.PublicationViewHolder>()
{
    private var publicationList: MutableList<PublicationFound> = list.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_publicationfound_list, parent, false)
        return PublicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicationViewHolder, position: Int) {
        val item = publicationList[position]
        holder.render(item, position)
    }

    override fun getItemCount(): Int = publicationList.size

    inner class PublicationViewHolder(publicationView: View): RecyclerView.ViewHolder(publicationView) {

        var binding = ItemPublicationfoundListBinding.bind(publicationView)
        var storage = AppDatabase.getStorageReference()
        fun render(item: PublicationFound, index: Int) {

            val pubText : String = "Publicado: "+item.fecha
            val recText : String = ""+item.nombre
            binding.fechaPublicacionFoundText.text = pubText
            binding.tipoPublicacionFoundText.text = item.tipo
            binding.recompensaPublicacionFoundText.text = recText
            binding.contextoPublicationFoundText.text = "Encontrado"
            storage.getReference("found").child(item.foto!!).downloadUrl
                .addOnSuccessListener {
                    Picasso.get()
                        .load(it.toString())
                        .fit()
                        .placeholder(R.drawable.place_holder)
                        .into(binding.imagePublicacionFound)
                }

            binding.itemPublicationFound.setOnClickListener {
                onClickItemSelected(publicationList[index])
            }

        }


    }



}