package com.dani.barkit.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.dani.barkit.api.DogApiClient
import com.dani.barkit.database.MyDatabaseContract.COLUMN_IMAGES_URL
import com.dani.barkit.database.MyDatabaseContract.COLUMN_IMAGE_BLOB
import com.dani.barkit.database.MyDatabaseContract.COLUMN_NAME
import com.dani.barkit.database.MyDatabaseContract.TABLE_NAME_BREED
import com.dani.barkit.`object`.BreedData
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import kotlin.random.Random

// MyDatabaseHelper que extiende de SQLiteOpenHelper para gestionar la creación, actualización y operaciones en la base de datos local

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, MyDatabaseContract.DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        //defino el modelo de tablas de la base de datos
        val createLinkImage = """
            CREATE TABLE ${MyDatabaseContract.TABLE_NAME_IMAGE} (
                ${MyDatabaseContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${MyDatabaseContract.COLUMN_NAME} TEXT,
                ${MyDatabaseContract.COLUMN_IMAGES_URL} TEXT
            )
        """.trimIndent()
        db.execSQL(createLinkImage)

        val createTableBreedData = """
            CREATE TABLE ${MyDatabaseContract.TABLE_NAME_BREED} (
                ${MyDatabaseContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${MyDatabaseContract.COLUMN_NAME} TEXT,
                ${MyDatabaseContract.COLUMN_IMAGES_URL} TEXT
            )
        """.trimIndent()
        db.execSQL(createTableBreedData)

        val createTableRazasQuery = """
            CREATE TABLE ${MyDatabaseContract.TABLE_NAME_RAZAS} (
                ${MyDatabaseContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${MyDatabaseContract.COLUMN_NOMBRE_RAZA} TEXT
            )
        """.trimIndent()
        db.execSQL(createTableRazasQuery)


        val createTableImagenesQuery = """
        CREATE TABLE ${MyDatabaseContract.TABLE_NAME_IMAGENES} (
            ${MyDatabaseContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${"nombre_raza"} TEXT,
            ${"imagen"} BLOB,
            ${"url"} BLOB
        )
    """.trimIndent()
        db.execSQL(createTableImagenesQuery)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //aun no implementado
    }


    fun insertarImagen(raza: String, urlImagen: String) {

        val db = writableDatabase

        // Inicia una transacción para mejorar el rendimiento
        db.beginTransaction()

        try {
            // Descargar la imagen y convertirla a un array de bytes (BLOB)
            val bitmap: Bitmap = Picasso.get().load(urlImagen).get()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()

            // Insertar en la tabla de imágenes
            val contentValues = ContentValues().apply {
                put(MyDatabaseContract.COLUMN_NOMBRE_RAZA, raza)
                put(MyDatabaseContract.COLUMN_BlOB_IMAGEN, urlImagen)
                put(MyDatabaseContract.COLUMN_IMAGE_BLOB, byteArray)
            }

            Log.i(
                "insertdb",
                "INSERT en ${MyDatabaseContract.TABLE_NAME_IMAGENES} [${COLUMN_IMAGES_URL} $urlImagen][${COLUMN_IMAGE_BLOB} $byteArray]"
            )
            db.insert(MyDatabaseContract.TABLE_NAME_IMAGENES, null, contentValues)

            // Marcar la transacción como exitosa
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.i("insertdb", "Error al insertar: $ex")
        } finally {
            // Finaliza la transacción, ya sea que haya sido exitosa o no
            db.endTransaction()
            // Cierra la base de datos
            db.close()
        }
    }

    fun obtenerPrimeraImagen(nombre_raza:String): Bitmap? {
        val db = readableDatabase
        var imagenBitmap: Bitmap? = null

        try {
            db.beginTransaction()

            val query = "SELECT * FROM ${MyDatabaseContract.TABLE_NAME_IMAGENES } where nombre_raza = ? LIMIT 3"
            val cursor = db.rawQuery(query, arrayOf(nombre_raza))
            var tam = cursor.columnCount
            if (cursor.moveToPosition(Random.nextInt(0, 3))) {
                val indiceColumnaImagen = cursor.getColumnIndex("url")
                val datosImagen = cursor.getBlob(indiceColumnaImagen)
                imagenBitmap = convertirBytesAImagen(datosImagen)
            }

            cursor.close()
            db.setTransactionSuccessful()
        } catch (ex: SQLiteException) {
            Log.e("selectDB", "Error al obtener la primera imagen: ${ex.message}")
        } finally {
            db.endTransaction()
            db.close()
        }

        return imagenBitmap
    }

    fun obtenerTodasLasImagenes(nombreRaza: String): List<Bitmap> {
        val db = readableDatabase
        val listaBitmaps = mutableListOf<Bitmap>()

        try {
            db.beginTransaction()

            val query = "SELECT * FROM ${MyDatabaseContract.TABLE_NAME_IMAGENES} WHERE nombre_raza = ?"
            val cursor = db.rawQuery(query, arrayOf(nombreRaza))

            while (cursor.moveToNext()) {
                val indiceColumnaImagen = cursor.getColumnIndex("url")
                val datosImagen = cursor.getBlob(indiceColumnaImagen)
                val imagenBitmap = convertirBytesAImagen(datosImagen)
                if (imagenBitmap != null) {
                    listaBitmaps.add(imagenBitmap)
                }
            }

            cursor.close()
            db.setTransactionSuccessful()
        } catch (ex: SQLiteException) {
            Log.e("selectDB", "Error al obtener las imágenes: ${ex.message}")
        } finally {
            db.endTransaction()
            db.close()
        }

        return listaBitmaps
    }

    // Función para convertir los datos de la imagen en un objeto Bitmap ya que vienen de un campo BLOB
    fun convertirBytesAImagen(datosImagen: ByteArray?): Bitmap? {
        return try {
            if (datosImagen != null) {

                Log.d("convertirBytesAImagen", "Tamaño de datosImagen: ${datosImagen.size}")
                BitmapFactory.decodeByteArray(datosImagen, 0, datosImagen.size)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("convertirBytesAImagen", "Error al convertir bytes a imagen: ${e.message}")
            null
        }
    }

    // Guardo las colecciones de raza en la base de datos
    fun guardarListBreedData(breedDataList: List<BreedData>) {

        val db = writableDatabase

        // Inicia una transacción para mejorar el rendimiento
        db.beginTransaction()
        try {
            for (breedData: BreedData in breedDataList) {
                // Almacenar en la base de datos
                val values = ContentValues().apply {
                    put(COLUMN_NAME, breedData.breedName)
                    put(COLUMN_IMAGES_URL, breedData.imageUrl.toString())
                }

                val newRowId = db.insert(TABLE_NAME_BREED, null, values)
                Log.d("ayuda", "Nuevo ID en la base de datos: $newRowId")
            }
            // Operaciones en la base de datos aquí
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

    }

    fun obtenerCantidadRegistrosImagenes(): Int {
        val db = readableDatabase
        var cantidadRegistros = 0

        try {
            db.beginTransaction()

            val query = "SELECT COUNT(*) FROM ${MyDatabaseContract.COLUMN_IMAGES_URL}"
            val cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                cantidadRegistros = cursor.getInt(0)
                Log.e("selectDB", "Cantidad de registros en la tabla de imágenes: $cantidadRegistros")
            }

            cursor.close()
            db.setTransactionSuccessful()
        } catch (ex: SQLiteException) {
            Log.e("selectDB", "Error al obtener la cantidad de registros: ${ex.message}")
        } finally {
            // Finaliza la transacción, ya sea que haya sido exitosa o no
            db.endTransaction()
            // Cierra la base de datos
            db.close()
        }

        return cantidadRegistros
    }

    // Con esta función escribo todos los datos necesarios en la base de datos
    fun guardarTodosLosDatos() {
        val client: DogApiClient = DogApiClient()
        var breedDatacolection: List<BreedData> = client.obtenerApiCompleta()
        guardarListBreedData(breedDatacolection);

        for (colecionRaza: BreedData in breedDatacolection) {
            var i = 0
            val max = 4 //maximo de fotos subidas
            for (url: String in colecionRaza.imageUrl) {
                if (i <= max) {
                    insertarImagen(colecionRaza.breedName, url)
                    i++
                } else {
                    continue
                }
            }

        }
        Log.d("ayuda", "BASE DE DATOS CARGADA")
    }

    fun obtenerNombresRazas(): List<String> {
        val db = readableDatabase
        val nombresRazas = mutableListOf<String>()

        try {
            db.beginTransaction()

            val query = "SELECT ${MyDatabaseContract.COLUMN_NAME} FROM ${MyDatabaseContract.TABLE_NAME_BREED} Group by ${MyDatabaseContract.COLUMN_NAME}"
            val cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {

                val nombreRaza = cursor.getString(0)
                nombresRazas.add(nombreRaza)
            }

            cursor.close()
            db.setTransactionSuccessful()
        } catch (ex: SQLiteException) {
            Log.e("selectDB", "Error al obtener nombres de razas: ${ex.message}")
        } finally {
            db.endTransaction()
            db.close()
        }

        return nombresRazas
    }


}