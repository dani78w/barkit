package com.dani.barkit.adapter


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.dani.barkit.Fotos
import com.dani.barkit.R
//Utilizo este recycler para mostrar las imagenes desde la base de datos , inserto bitmaps que anteriormente he convertido de BLOPs
class BitmapRecyclerViewAdapter(
    private val context: Context,
    private val pairlist: List<Pair<String,Bitmap?>>,
) : RecyclerView.Adapter<BitmapRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.foto_raza)
        val textView :TextView = itemView.findViewById(R.id.nombre_raza)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.db_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = pairlist[position]
        holder.textView.setText(data.first)
        holder.imageView.setImageBitmap(data.second)
        holder.itemView.setOnClickListener {

                //AQUI ABRO LA ACTIVIDAD FOTO CON EL MODO DE BASES DE DATOS PARA LUEGO PROPAGARLO AL FRAGMENT
                val intent = Intent(context, Fotos::class.java)
                intent.putExtra("name", data.first)
                intent.putExtra("mode","database")
                context.startActivity(intent)


            }
    }

    override fun getItemCount(): Int {
        return pairlist.size
    }
}
