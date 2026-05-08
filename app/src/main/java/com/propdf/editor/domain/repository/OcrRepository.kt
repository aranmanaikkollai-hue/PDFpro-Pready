package com.propdf.editor.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.domain.model.OcrResult

interface OcrRepository {
    suspend fun recognizeText(bitmap: Bitmap): OcrResult
    suspend fun recognizeTextFromPdf(uri: Uri, pageIndex: Int): OcrResult
}
