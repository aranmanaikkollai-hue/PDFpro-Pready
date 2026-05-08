package com.propdf.editor.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.data.ocr.TesseractOcrEngine
import com.propdf.editor.domain.model.OcrResult
import com.propdf.editor.domain.repository.OcrRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrRepositoryImpl @Inject constructor(
    private val tesseractEngine: TesseractOcrEngine
) : OcrRepository {
    override suspend fun recognizeText(bitmap: Bitmap): OcrResult = tesseractEngine.recognize(bitmap)
    override suspend fun recognizeTextFromPdf(uri: Uri, pageIndex: Int): OcrResult {
        throw NotImplementedError()
    }
}
