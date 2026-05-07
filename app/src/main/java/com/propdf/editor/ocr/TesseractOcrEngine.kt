package com.propdf.editor.ocr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.domain.model.OcrResult
import com.propdf.editor.domain.model.TextBlock
import com.propdf.editor.domain.repository.OcrRepository
import com.propdf.editor.utils.FileUtils
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TesseractOcrEngine @Inject constructor(
    private val context: Context
) : OcrRepository {

    private var tessApi: TessBaseAPI? = null
    private val dataPath: File by lazy {
        File(context.filesDir, "tesseract").apply { mkdirs() }
    }

    override suspend fun recognizeText(bitmap: Bitmap): Result<OcrResult> =
        withContext(Dispatchers.Default) {
            runCatching {
                ensureInitialized()

                val api = tessApi ?: throw IllegalStateException("Tesseract not initialized")

                api.setImage(bitmap)
                val text = api.utF8Text
                val confidence = api.meanConfidence().toFloat()

                // Get text blocks with bounding boxes
                val iterator = api.resultIterator
                val blocks = mutableListOf<TextBlock>()
                val level = TessBaseAPI.PageIteratorLevel.RIL_WORD

                iterator?.let {
                    do {
                        val word = it.getUTF8Text(level)
                        val rect = it.getBoundingRect(level)
                        if (word != null && rect != null) {
                            blocks.add(TextBlock(word, rect))
                        }
                    } while (it.next(level))
                }

                api.clear()

                OcrResult(
                    text = text,
                    confidence = confidence,
                    blocks = blocks
                )
            }
        }

    override suspend fun recognizeTextFromUri(uri: Uri): Result<OcrResult> =
        withContext(Dispatchers.IO) {
            runCatching {
                val tempFile = FileUtils.uriToTempFile(context, uri)
                    ?: throw Exception("Cannot read image")

                val bitmap = android.graphics.BitmapFactory.decodeFile(tempFile.absolutePath)
                    ?: throw Exception("Cannot decode image")

                val result = recognizeText(bitmap)
                bitmap.recycle()
                result.getOrThrow()
            }
        }

    override suspend fun isLanguageDataAvailable(language: String): Boolean {
        return File(dataPath, "tessdata/$language.traineddata").exists()
    }

    override suspend fun downloadLanguageData(language: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val tessdataDir = File(dataPath, "tessdata").apply { mkdirs() }
                val trainedDataFile = File(tessdataDir, "$language.traineddata")

                if (trainedDataFile.exists()) return@runCatching

                // Download from GitHub tessdata repository
                val url = "https://github.com/tesseract-ocr/tessdata/raw/main/$language.traineddata"
                val connection = java.net.URL(url).openConnection()
                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                connection.getInputStream().use { input ->
                    FileOutputStream(trainedDataFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }

    private fun ensureInitialized() {
        if (tessApi == null) {
            tessApi = TessBaseAPI().apply {
                // Initialize with English by default
                val initSuccess = init(dataPath.absolutePath, "eng")
                if (!initSuccess) {
                    throw IllegalStateException("Failed to initialize Tesseract")
                }
                pageSegMode = TessBaseAPI.PageSegMode.PSM_AUTO
            }
        }
    }

    fun release() {
        tessApi?.end()
        tessApi = null
    }
}
