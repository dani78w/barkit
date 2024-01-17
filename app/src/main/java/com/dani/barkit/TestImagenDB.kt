package com.dani.barkit

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dani.barkit.adapter.BitmapRecyclerViewAdapter
import com.dani.barkit.database.MyDatabaseHelper
import com.dani.barkit.`object`.Breed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//ESTA CLASE LA HE USADO DE TEST PARA LA BASE DE DATOS
class TestImagenDB : AppCompatActivity() {
    private lateinit var image: ImageView
    private lateinit var textView :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_imagen_db)

        textView = findViewById(R.id.textView)
        textView.setText("asdasd")
        image = findViewById(R.id.imageView)
        var breeedlist = mutableListOf<Breed>()

        var feed: RecyclerView = findViewById(R.id.prueba);
        feed.itemAnimator = DefaultItemAnimator()
        feed.layoutManager = LinearLayoutManager(this)
        val adapter = BitmapRecyclerViewAdapter(this@TestImagenDB, listOf())

        feed.adapter = adapter

        // Utiliza el contexto del ciclo de vida de la actividad
        GlobalScope.launch(Dispatchers.IO) {

            try {
                val razas = MyDatabaseHelper(this@TestImagenDB).obtenerNombresRazas()
                val imagenByteArray = MyDatabaseHelper(this@TestImagenDB).obtenerPrimeraImagen("basenji")
                if (imagenByteArray != null) {

                }
                MyDatabaseHelper(this@TestImagenDB).obtenerCantidadRegistrosImagenes()
                // Verifica si el ByteArray no es nulo antes de decodificar y mostrar la imagen
                if (imagenByteArray != null) {
                    val bitmap = imagenByteArray//BitmapFactory.decodeByteArray(imagenByteArray, 0, imagenByteArray.size)

                    // Actualiza el ImageView en el hilo principal

                    image.setImageBitmap(bitmap)
                    textView.setText(bitmap.toString())
                    var listaDatos = mutableListOf<Pair<String,Bitmap?>>()
                    for(raza:String in razas){
                        listaDatos.add(Pair(raza,MyDatabaseHelper(this@TestImagenDB).obtenerPrimeraImagen(raza)))
                    }


                    val adapter2 = BitmapRecyclerViewAdapter(this@TestImagenDB,listaDatos)
                    //val adapter = BitmapRecyclerViewAdapter(this@TestImagenDB,Pair("basenji",MyDatabaseHelper(this@TestImagenDB). obtenerTodasLasImagenes("basenji")) )
                    feed.swapAdapter(adapter2,true)


                } else {
                    Log.d("imagenException", "El ByteArray de la imagen es nulo.")
                }
            } catch (ex: Exception) {
                // Maneja la excepción de manera más detallada, muestra un mensaje de error, etc.
                Log.e("imagenException", "Error al cargar la imagen: $ex")
            }
        }

    }
}
