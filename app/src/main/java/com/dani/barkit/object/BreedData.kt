package com.dani.barkit.`object`


//raza con sus fotos
class BreedData{

    var breedName: String =  ""
    var imageUrl: List<String> = listOf<String>()

    constructor(breedName:String,imageUrl:List<String>){
        this.imageUrl = imageUrl
        this.breedName = breedName
    }
}