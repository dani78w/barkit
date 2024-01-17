package com.dani.barkit.database
//tags de las tablas
object MyDatabaseContract {
    const val DATABASE_NAME = "mi_base_de_datos.db"
    const val TABLE_NAME_RAZAS = "razas"
    const val COLUMN_ID = "_id"
    const val COLUMN_NOMBRE_RAZA = "nombre_raza"
    const val TABLE_NAME_IMAGENES= "imagenes"
    const val COLUMN_URL_IMAGEN = "url"
    const val COLUMN_BlOB_IMAGEN = "imagen"

    const val TABLE_NAME_BREED = "breed_table"
    //const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_IMAGES_URL = "image_url"

    const val TABLE_NAME_IMAGE = "image_table"
    const val COLUMN_IMAGE_URL = "imagen"
    const val COLUMN_IMAGE_BLOB = "url"
    // Agrega más columnas según tus necesidades
}