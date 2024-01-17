package com.dani.barkit.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.RecyclerView
import com.dani.barkit.R
import com.dani.barkit.`object`.Breed
import com.bumptech.glide.Glide
import com.dani.barkit.Fotos

//Utilizo este recycler para mostrar las imagenes desde la api con la libreria Glance
class UrlImagesRecyclerViewAdapter(actividad: Activity) : RecyclerView.Adapter<UrlImagesRecyclerViewAdapter.ViewHolder>() {
    private var dogList: List<Breed> = emptyList()
    private var actividad: Activity = actividad

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.post_item, parent, false)) {

        private var imageView: ImageView = itemView.findViewById(R.id.foto_raza)
        private var breedTextView: TextView = itemView.findViewById(R.id.nombre_raza)

        fun bind(breed: Breed) {

            // Configura las vistas según las propiedades del perro
            Glide.with(itemView.context)
                .load(breed.imageUrl)
                .override(Constraints.LayoutParams.MATCH_PARENT)
                .into(imageView)
            breedTextView.text = breed.breedName.toUpperCase()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dogList[position])
        holder.itemView.setOnClickListener {
            // Manejar el clic del elemento aquí
            // Por ejemplo, puedes abrir una nueva actividad o realizar otra acción

                val intent = Intent(actividad, Fotos::class.java)
                intent.putExtra("name", dogList.get(position).breedName)
                intent.putExtra("mode","api")
                actividad.startActivity(intent)


        }}

        override fun getItemCount(): Int = dogList.size
        fun actualizarDatos(nuevaLista: MutableList<Breed>) {
            dogList = nuevaLista
            notifyDataSetChanged()
        }
        fun isDatabaseExists(context: Context, dbName: String): Boolean {
            val dbPath = context.getDatabasePath(dbName)
            return dbPath.exists()
        }
    }

