package com.dani.barkit.`object`

import android.graphics.Bitmap
// raza
class Breed{
    var imageUrl: String = ""
    var breedName: String =  ""

    constructor(imageUrl:String, breedName:String){
        this.imageUrl = imageUrl
        this.breedName = breedName
    }
}