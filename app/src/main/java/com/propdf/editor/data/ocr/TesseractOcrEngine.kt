package com.propdf.editor.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import com.googlecode.tesseract.android.TessBaseAPI
import com.propdf.editor.domain.model.OcrResult
import com.propdf.editor.domain.model.OcrWord
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TesseractOcrEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tessBaseAPI: TessBaseAPI? = null
    private val dataPath: String by lazy {
        File(context.filesDir, "tesseract").absolutePath
    }

    fun initialize(language: String = "eng"): Boolean {
        return try {
            val tessDir = File(dataPath, "tessdata")
            if (!tessDir.exists()) tessDir.mkdirs()
            tessBaseAPI = TessBaseAPI()
            tessBaseAPI!!.init(dataPath, language)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun recognize(bitmap: Bitmap): OcrResult = withContext(Dispatchers.Default) {
        val api = tessBaseAPI ?: throw IllegalStateException("Tesseract not initialized")
        
        api.setImage(bitmap)
        val text = api.utF8Text ?: ""
        val confidence = api.meanConfidence().toFloat()
        
        val words = mutableListOf<OcrWord>()
        val resultIterator = api.resultIterator
        
        if (resultIterator != null) {
            resultIterator.begin()
            do {
                val wordText = resultIterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD) ?: ""
                val wordConf = resultIterator.confidence(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                val rect = Rect()
                resultIterator.getBoundingBox(TessBaseAPI.PageIteratorLevel.RIL_WORD, rect)
                words.add(OcrWord(wordText, wordConf, rect))
            } while (resultIterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD))
            resultIterator.delete()
        }

        OcrResult(text, confidence, words)
    }

    fun destroy() {
        tessBaseAPI?.end()
        tessBaseAPI = null
    }
}
