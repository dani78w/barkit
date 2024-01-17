package com.dani.barkit.task

import android.os.AsyncTask
import com.dani.barkit.adapter.UrlImagesRecyclerViewAdapter
import com.dani.barkit.api.DogApiClient
import com.dani.barkit.`object`.ApiResponse
import com.dani.barkit.`object`.Breed
import com.dani.barkit.`object`.DogApiQuerysContract
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

//ESTA CLASE ES UNA PRUEBA QUE HICE AL PRINCIPIO UN POCO ANARQUÍCA!!
class FeedTask(private val adapter: UrlImagesRecyclerViewAdapter, private var listaDePerros: MutableList<Breed>) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String? {
        /* una random
        var query :String = DogApiQuery().randomBreed
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(query)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body
                // En tu código de manejo de la respuesta
                val gson = Gson()
                val jsonString = response.body?.string() // mensaje JsonString
                val apiResponse: ApiResponse = gson.fromJson(jsonString, ApiResponse::class.java)// Convertir la cadena JSON a un objeto ApiResponse
                apiResponse.message
                listaDePerros.add(Breed(apiResponse.message,apiResponse.message.substringAfter("breeds/").substringBefore("/")))
                return "Prueba"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "false"

         */
        var client: DogApiClient = DogApiClient()
        client.rellenarNombres();
        listaDePerros.addAll(client.feedContent)


        //rellenarNombres()

        return "true"
    }
    fun rellenarNombres(){
        var query :String = DogApiQuerysContract().allBreeds
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(query).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                response.body
                // En tu código de manejo de la respuesta
                val gson = Gson()
                val jsonString = response.body?.string() // mensaje JsonString
                if (jsonString != null) {
                    var listaRazas :List<String> = transformarJsonALista(jsonString)

                    for (a:String in listaRazas){
                        listaDePerros.add(Breed("https://i.pinimg.com/736x/01/16/12/0116128680b0de1ed64aa1ae93804aa9.jpg",a))
                    }


                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    override fun onPostExecute(result: String?) {

            adapter.notifyDataSetChanged()
            RandomFiller(adapter,listaDePerros).execute()






        //adapter se actualiza con breeed
    }
    fun obtenerUrlImagenParaRaza(raza: String): String {
        val client = OkHttpClient()


        var query: String = DogApiQuerysContract().randomOneOfBreed(raza)

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

            var link = apiResponse.message







            return link
        }



        // Simulamos una solicitud a una API para obtener la URL de la imagen
        return "https://dog.ceo/api/breeds/image/random?breed=$raza"
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
    // Función rellenarFotos utilizando coroutines

}