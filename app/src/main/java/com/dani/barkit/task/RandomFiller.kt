package com.dani.barkit.task

import android.os.AsyncTask
import com.dani.barkit.adapter.UrlImagesRecyclerViewAdapter
import com.dani.barkit.`object`.ApiResponse
import com.dani.barkit.`object`.Breed
import com.dani.barkit.`object`.DogApiQuerysContract
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class RandomFiller(
    private val adapter: UrlImagesRecyclerViewAdapter,
    private var listaDePerros: MutableList<Breed>
) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String? {
        rellenarFotos()

        return "true"

    }

    override fun onPostExecute(result: String?) {
        adapter.notifyDataSetChanged()
        //adapter se actualiza con breeed
    }

    fun rellenarFotos() {
        try {


            val client = OkHttpClient()

            for (raza: Breed in this.listaDePerros) {
                var query: String = DogApiQuerysContract().randomOneOfBreed(raza.breedName)

                val request = Request.Builder()
                    .url(query)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body
                    // En tu código de manejo de la respuesta
                    val gson = Gson()
                    val jsonString = response.body?.string() // mensaje JsonString
                    val apiResponse: ApiResponse = gson.fromJson(
                        jsonString,
                        ApiResponse::class.java
                    )// Convertir la cadena JSON a un objeto ApiResponse
                    var imagenAleatoria = Breed(
                        apiResponse.message,
                        apiResponse.message.substringAfter("breeds/").substringBefore("/")
                    )
                    var url: String = apiResponse.message
                    listaDePerros.remove(raza)
                    listaDePerros.add(imagenAleatoria)


                }

            }
        }catch (ex:java.lang.Exception){
            ex.toString()
        }
    }
    fun transformarJsonALista(jsonString: String): List<String> {
        val listaDeRazas = mutableListOf<String>()

        // Parsea la respuesta JSON
        val jsonResponse = JSONObject(jsonString)

        // Obtiene el objeto "message"
        val message = jsonResponse.getJSONObject("message")

        // Itera sobre las claves del objeto "message" (que son las razas)
        for (raza in message.keys()) {
            // Agrega la raza a la lista
            listaDeRazas.add(raza)
        }

        return listaDeRazas
    }
    class RellenarFotos : Thread() {

        override fun run() {
            // Código que se ejecutará en el nuevo hilo
            for (i in 1..5) {
                println("Nuevo hilo: $i")
                Thread.sleep(1500) // Introduce una pausa de 1.5 segundos
            }
        }
    }
}