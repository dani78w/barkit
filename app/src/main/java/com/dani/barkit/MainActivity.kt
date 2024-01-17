package com.dani.barkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dani.barkit.adapter.BitmapRecyclerViewAdapter
import com.dani.barkit.adapter.UrlImagesRecyclerViewAdapter
import com.dani.barkit.controller.Controlador
import com.dani.barkit.`object`.Breed


class MainActivity : AppCompatActivity() {
    private lateinit var controlador: Controlador
    private lateinit var text: TextView
    private lateinit var card_offline: CardView
    private lateinit var card_progress: CardView
    private lateinit var card_back:CardView
    private lateinit var indicador_descarga_db :CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ocultar la barra de notificaciones
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_main)



        var cache: MutableList<Breed> = mutableListOf<Breed>();


        //controlador
        controlador = ViewModelProvider(this).get(Controlador::class.java)
        controlador.setActivity(this);
        controlador.lanzarConsultasDB();
        controlador.lanzarConsultasApi();

        val adapterOnline = UrlImagesRecyclerViewAdapter(this)
        val adapterOffline = BitmapRecyclerViewAdapter(this, listOf())

        var recyclerView: RecyclerView = findViewById(R.id.feed_recycler);
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterOnline

        controlador.adapterLiveData.observe(this,{ nuevoAdapter ->
            recyclerView.adapter = nuevoAdapter
        })

        text = findViewById(R.id.insertions)
        text.setText("Obteniendo razas")

        controlador.listaFinal.observe(this, { resultado ->
            adapterOnline.actualizarDatos(resultado)
            if(!controlador.numeroImagenesDescargadas.value.toString().equals("null")){
                text.setText(controlador.numeroImagenesDescargadas.value.toString() + " razas en sqlite")
            }else{
                text.setText(" Razas en sqlite")
            }

            cache.addAll(resultado)

        })

        card_offline = findViewById(R.id.internet)
        card_offline.setVisibility(View.GONE);
        card_progress = findViewById(R.id.indicador_descarga)
        card_progress.setVisibility(View.VISIBLE)
        card_back = findViewById(R.id.backButton)
        indicador_descarga_db = findViewById(R.id.indicador_descarga_db)

        controlador.estadoDescarga.observe(this, { resultado ->
            //inserto el ultimo en cargar

            if (resultado) {
                card_offline.setVisibility(View.VISIBLE);
                card_progress.setVisibility(View.GONE);

                card_offline.setOnClickListener {
                    controlador.procesarclicado("abrir")
                }
            }else{
                card_offline.setVisibility(View.INVISIBLE);
                card_progress.setVisibility(View.VISIBLE)
            }


        })
        controlador.modo.observe(this,{ resultado ->
            if(resultado){
                card_back.setVisibility(View.VISIBLE)
                card_back.setOnClickListener {
                    controlador.procesarclicado("cerrar")
                }
            }else{
                card_back.setVisibility(View.INVISIBLE)
            }

        })

        controlador.cargando.observe(this,{ resultado ->
            if (resultado){
                indicador_descarga_db.setVisibility(android.view.View.VISIBLE)


            }else{
                indicador_descarga_db.setVisibility(android.view.View.INVISIBLE)
            }
        })
//val feedTask = FeedTask(adapter, perros);
//feedTask.execute()


        Log.d("ayuda", "Ahora se ha cargado el main entero")
        //var intent = Intent(this,TestImagenDB::class.java)
        //startActivity(intent)
// Ejecutar la tarea en segundo plano

//controlador.rellenarNombres(feedContent)


    }



}


