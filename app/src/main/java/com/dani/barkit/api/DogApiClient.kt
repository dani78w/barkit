package com.dani.barkit.api

import android.util.Log
import com.dani.barkit.`object`.Breed
import com.dani.barkit.`object`.BreedData
import com.dani.barkit.`object`.DogApiQuerysContract
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
// Maneja las todas solicitudes a la API de perros para obtener información sobre razas y fotos.
class DogApiClient {
    var feedContent :MutableList<Breed> = mutableListOf<Breed>();
    var selectedBreedFotos :MutableList<String> = mutableListOf<String>()

    fun rellenarNombres():List<String>{

            // Lógica de la petición a la API
        var listaRazasResoult :List<String> = listOf<String>()
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
                    listaRazasResoult=listaRazas
                    for (a:String in listaRazas){
                        feedContent.add(Breed("https://i.pinimg.com/736x/01/16/12/0116128680b0de1ed64aa1ae93804aa9.jpg",a))
                    }


                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listaRazasResoult
        }
    fun descargarTodas(raza:String):List<String>{

        // Lógica de la petición a la API
        var resoult :MutableList<String> = mutableListOf<String>()
        var query :String = DogApiQuerysContract().everyOneOfBreed(raza)
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
                    var listaFotos :List<String> = transformarJsonALista2(jsonString)

                    for (a:String in listaFotos){
                        selectedBreedFotos.add(a)
                        resoult.add(a)
                    }


                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resoult
    }
    fun obtenerApiCompleta():List<BreedData>{
            var nombreRazas = rellenarNombres()
            var enlacesFotosDeRaza = listOf<String>()
            var colecciones = mutableListOf<BreedData>()

            for(raza:String in nombreRazas){
                enlacesFotosDeRaza = descargarTodas(raza)
                colecciones.add(BreedData(raza,enlacesFotosDeRaza))
                //Log.d("ayuda","nombre y links :"+ colecciones.last().breedName +" :"+colecciones.last().imageUrl.toString())
            }



            Log.d("ayuda", "rellenarDB:DONE ")
            return colecciones
    }

    //utilidades
    fun transformarJsonALista2(jsonString: String): List<String> {
        val listaDeImagenes = mutableListOf<String>()
        val jsonResponse = JSONObject(jsonString)
        val messageArray = jsonResponse.getJSONArray("message")

        // Itera sobre los elementos de la lista de imágenes
        for (i in 0 until messageArray.length()) {
            // Agrega cada URL de imagen a la lista
            listaDeImagenes.add(messageArray.getString(i))
        }

        return listaDeImagenes
    }
    fun transformarJsonALista(jsonString: String): List<String> {
        val listaDeRazas = mutableListOf<String>()
        val jsonResponse = JSONObject(jsonString)
        val message = jsonResponse.getJSONObject("message")

        // Itera sobre las claves del objeto "message" (que son las razas)
        for (raza in message.keys()) {
            // Agrega la raza a la lista
            listaDeRazas.add(raza)
        }

        return listaDeRazas
    }

}