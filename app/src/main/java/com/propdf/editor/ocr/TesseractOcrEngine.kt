package com.propdf.editor.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class TesseractOcrEngine(private val context: Context) {

    private val tessBaseApi = TessBaseAPI()
    private var isInitialized = false

    suspend fun initialize(language: String = "eng"): Boolean = withContext(Dispatchers.IO) {
        val dataPath = File(context.filesDir, "tesseract")
        val tessData = File(dataPath, "tessdata")
        if (!tessData.exists()) {
            tessData.mkdirs()
        }

        val trainedDataFile = File(tessData, "$language.traineddata")
        if (!trainedDataFile.exists()) {
            try {
                context.assets.open("tessdata/$language.traineddata").use { input ->
                    FileOutputStream(trainedDataFile).use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                return@withContext false
            }
        }

        isInitialized = tessBaseApi.init(dataPath.absolutePath, language)
        return@withContext isInitialized
    }

    suspend fun recognizeText(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            throw IllegalStateException("Tesseract not initialized")
        }
        tessBaseApi.setImage(bitmap)
        val text = tessBaseApi.utF8Text ?: ""
        tessBaseApi.clear()
        return@withContext text
    }

    suspend fun recognizeTextFromUri(uri: Uri): String = withContext(Dispatchers.IO) {
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        } ?: return@withContext ""
        return@withContext recognizeText(bitmap)
    }

    fun release() {
        tessBaseApi.stop()
    }
}
