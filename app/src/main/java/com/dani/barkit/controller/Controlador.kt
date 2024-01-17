package com.dani.barkit.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.dani.barkit.MainActivity
import com.dani.barkit.adapter.BitmapRecyclerViewAdapter
import com.dani.barkit.api.DogApiClient
import com.dani.barkit.database.MyDatabaseHelper
import com.dani.barkit.`object`.ApiResponse
import com.dani.barkit.`object`.Breed
import com.dani.barkit.`object`.BreedData
import com.dani.barkit.`object`.DogApiQuerysContract
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

// Esta Clase Controlador que extiende de ViewModel sirve para gestionar la lógica y datos de la interfaz de usuario
class Controlador : ViewModel() {


    private val tag:String ="ControladorVistas"
    // Bloque de inicialización y declaración de variables de control
    var client: DogApiClient = DogApiClient()
    private val _listaFinal = MutableLiveData<MutableList<Breed>>()
    private val _estadoDescarga = MutableLiveData<Boolean>(false)
    private val _numeroImagenesDescargadas = MutableLiveData<Int>()
    private val _modo = MutableLiveData<Boolean>(false)
    private val _cargando = MutableLiveData<Boolean>(false)
    private val _adapterLiveData = MutableLiveData<RecyclerView.Adapter<*>>()

    //live data para notificar a la actividad de los cambios en las anteriores variables
    val adapterLiveData: LiveData<RecyclerView.Adapter<*>> = _adapterLiveData
    val listaFinal: LiveData<MutableList<Breed>> get() = _listaFinal
    val estadoDescarga: LiveData<Boolean> get() = _estadoDescarga
    val numeroImagenesDescargadas : LiveData<Int> get() = _numeroImagenesDescargadas
    val modo: LiveData<Boolean> get() = _modo
    val cargando: LiveData<Boolean> get() = _cargando

    //variable que uso para la propagación de el contexto en las corrutinas
    private lateinit var activity: Activity

    // Función para lanzar consultas a la base de datos local
    fun lanzarConsultasDB() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Si el telefono ya tiene descargada la base de datos
                if(isDatabase(activity,"mi_base_de_datos.db")){
                                                      //check
                    val imagenByteArray = MyDatabaseHelper(activity).obtenerPrimeraImagen("basenji")   //check
                    val todasDeUnaRaza = MyDatabaseHelper(activity). obtenerTodasLasImagenes("basenji")  //check
                    val razas = MyDatabaseHelper(activity).obtenerNombresRazas()

                    _estadoDescarga.postValue(true)
                    _numeroImagenesDescargadas.postValue(
                        razas.size
                    )

                }else{
                    //llamo a las funciones de mi Database helper para cargar todas las imagenes de la api en la base de datos local
                    Log.d("existe", "Creando base de datos")
                    val client: DogApiClient = DogApiClient()
                    var breedDatacolection:List<BreedData> = client.obtenerApiCompleta()

                    MyDatabaseHelper(activity).guardarTodosLosDatos()
                    _estadoDescarga.postValue(true)
                    val razas = MyDatabaseHelper(activity).obtenerNombresRazas()

                    _numeroImagenesDescargadas.postValue(
                        razas.size
                    )

                    Log.d(tag, client.feedContent.toString())
                }
            } catch (e: Exception) {
                Log.d(tag, e.toString())
            }

        }
    }
    fun lanzarConsultasApi() {
        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    val client: DogApiClient = DogApiClient()
                    client.rellenarNombres()
                    _listaFinal.postValue(client.feedContent)
                    Log.d("ayuda", client.feedContent.toString())
                }
                // Llamar a la función proporcionada con el resultado en el hilo principal

            } catch (e: Exception) {
                e.printStackTrace()
                // Manejar cualquier excepción aquí
            } finally {
                Log.d("ayuda", "NOMBRES CARGADOS")
                try {
                    withContext(Dispatchers.IO) {
                        fotos()
                        Log.d("ayuda", "FOTOS CARGADAS")

                    }
                } catch (ex: java.lang.Exception) {
                    Log.d("ayuda", ex.toString())
                }
            }
        }
    }
    private fun fotos() {
        // Hacer una copia de la lista para evitar ConcurrentModificationException
        val razas: MutableList<Breed> = _listaFinal.value?.toMutableList() ?: mutableListOf()

        for (raza: Breed in razas) {
            val client = OkHttpClient()
            val query: String = DogApiQuerysContract().randomOneOfBreed(raza.breedName)

            val request = Request.Builder().url(query).build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // En tu código de manejo de la respuesta
                val gson = Gson()
                val jsonString = response.body?.string() // mensaje JsonString
                val apiResponse: ApiResponse = gson.fromJson(
                    jsonString,
                    ApiResponse::class.java
                )// Convertir la cadena JSON a un objeto ApiResponse
                val imagenAleatoria = Breed(
                    apiResponse.message,
                    apiResponse.message.substringAfter("breeds/").substringBefore("/")
                )
                val url: String = apiResponse.message

                // Reemplazar la raza actual con la imagen aleatoria
                val index = razas.indexOf(raza)
                if (index != -1) {
                    razas[index] = imagenAleatoria
                }
                //almacenarla en BlOB en la base de datos
                /*MyDatabaseHelper(activity).insertarImagen(
                    imagenAleatoria.breedName,
                    imagenAleatoria.imageUrl
                )
                MyDatabaseHelper(activity).obtenerDatosImagen(imagenAleatoria.breedName)*/
                _listaFinal.postValue(razas)
            }
        }

        // Actualizar la lista final después de la iteración
        _listaFinal.postValue(razas)
        //hacer propio para api _estadoDescarga.postValue(true)
    }
    fun isDatabase(context: Context, dbName: String): Boolean {
        val dbPath = context.getDatabasePath(dbName)
        return dbPath.exists()
    }
    fun procesarclicado(control:String) {
        if(control.equals("abrir")){
        _modo.postValue(true)
            _cargando.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val razas = MyDatabaseHelper(activity).obtenerNombresRazas()
                val imagenByteArray = MyDatabaseHelper(activity).obtenerPrimeraImagen("basenji")
                if (imagenByteArray != null) {

                }
                MyDatabaseHelper(activity).obtenerCantidadRegistrosImagenes()
                // Verifica si el ByteArray no es nulo antes de decodificar y mostrar la imagen
                if (imagenByteArray != null) {
                    val bitmap =
                        imagenByteArray//BitmapFactory.decodeByteArray(imagenByteArray, 0, imagenByteArray.size)

                    // Actualiza el ImageView en el hilo principal


                    var listaDatos = mutableListOf<Pair<String, Bitmap?>>()
                    for (raza: String in razas) {
                        try {
                            listaDatos.add(
                                Pair(
                                    raza,
                                    MyDatabaseHelper(activity).obtenerPrimeraImagen(raza)
                                )
                            )
                        } catch (ex: SQLiteException) {
                            Log.d("SQLiteException", ex.toString())
                        }

                    }


                    val adapter2 = BitmapRecyclerViewAdapter(activity, listaDatos)

                    cambiarAdaptador(adapter2)


                } else {
                    Log.d("imagenException", "El ByteArray de la imagen es nulo.")
                }
            } catch (ex: Exception) {
                // Maneja la excepción de manera más detallada, muestra un mensaje de error, etc.
                Log.e("imagenException", "Error al cargar la imagen: $ex")
            }

            Log.d("ayuda", "has clicado ")
            _cargando.postValue(false)

        }
        }else if (control.equals("cerrar")){
            _modo.postValue(false)
            var reinicio:Intent = Intent(activity,MainActivity::class.java)
            activity.startActivity(reinicio)
            this.activity.finish()
        }
    }
    fun cambiarAdaptador(nuevoAdapter: RecyclerView.Adapter<*>) {

        _adapterLiveData.postValue( nuevoAdapter )
    }
    fun setActivity(activity: Activity) {
        this.activity = activity
    }

}
