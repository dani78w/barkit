package com.dani.barkit

import ViewPagerAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dani.barkit.adapter.ViewPagerAdapterDb
import com.dani.barkit.api.DogApiClient
import com.dani.barkit.database.MyDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Fotos : AppCompatActivity() {
    lateinit var razaTextView: TextView
    lateinit var viewPager: ViewPager2
    var nombre: String = ""
    var mode: String = "api"
    fun isDatabaseExists(context: Context, dbName: String): Boolean {
        val dbPath = context.getDatabasePath(dbName)
        return dbPath.exists()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotos)
        //ocultar la barra de notificaciones y
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        razaTextView = findViewById(R.id.titulo_visor)
        val intent = intent
        if (intent != null) {
            this.nombre = intent.getStringExtra("name").toString()
            this.mode = intent.getStringExtra("mode").toString()
            Log.d("ayuda", nombre.toString())
            razaTextView.setText(nombre)
            // Hacer algo con el nombre
        }
        viewPager = findViewById(R.id.view_pager)
        if (mode.equals("api")) {
            getListOfImageUrlsFromApi()
        } else {
            getimagenByteArrayFromDb()
            Log.d("ayuda", "has clicado desde db: ")
        }
        // Reemplaza esto con la lógica de tu API


    }

    private fun getListOfImageUrlsFromApi() {
        //uso la lógica de cargarlas desde Glade
        GlobalScope.launch(Dispatchers.Main) {

            val client: DogApiClient = DogApiClient()
            val images = withContext(Dispatchers.IO) {

                client.descargarTodas(nombre)
            }

            val imagePagerAdapter = ViewPagerAdapter(this@Fotos, client.selectedBreedFotos)
            viewPager.adapter = imagePagerAdapter
            viewPager.setPageTransformer(DepthPageTransformer())

        }

    }

    private fun getimagenByteArrayFromDb() {
        //uso la lógica de transformar los BLOBS en bitmaps
        GlobalScope.launch(Dispatchers.Main) {

            val imagenes = withContext(Dispatchers.IO) {
                MyDatabaseHelper(this@Fotos).obtenerTodasLasImagenes(nombre)

            }
            val imagePagerAdapter = ViewPagerAdapterDb(this@Fotos, imagenes)
            viewPager.adapter = imagePagerAdapter
            viewPager.setPageTransformer(DepthPageTransformer())


        }

    }

    //es una clase para hacer una animación bonita
    class DepthPageTransformer : ViewPager2.PageTransformer {
        private val MIN_SCALE = 0.75f

        override fun transformPage(view: android.view.View, position: Float) {
            val pageWidth = view.width

            when {
                position < -1 -> view.alpha = 0f
                position <= 0 -> {
                    view.alpha = 1f
                    view.translationX = 0f
                    view.scaleX = 1f
                    view.scaleY = 1f
                }
                position <= 1 -> {
                    view.alpha = 1 - position
                    view.translationX = pageWidth * -position
                    val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
                    view.scaleX = scaleFactor
                    view.scaleY = scaleFactor
                }
                else -> view.alpha = 0f
            }
        }
    }
}

