package com.dani.barkit.`object`

//tags de las direcciones en las que llamar a la api
class DogApiQuerysContract {
    private val _allbreeds : String = "https://dog.ceo/api/breeds/list/all"
    private var _byBreed : String = "https://dog.ceo/api/breed/hound/images"

    val allBreeds: String
        get() {
            return _allbreeds
        }
    fun randomOneOfBreed(breed: String) : String{
        var resoult = _byBreed.replace("hound",breed)+"/random"
        return resoult
    }
    fun everyOneOfBreed(breed: String) : String{
        var resoult = _byBreed.replace("hound",breed.substringBefore("-"))
        return resoult
    }

}