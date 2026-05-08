package com.propdf.editor.domain.usecase

import android.graphics.Bitmap
import com.propdf.editor.domain.model.OcrResult
import com.propdf.editor.domain.repository.OcrRepository
import javax.inject.Inject

class PerformOcrUseCase @Inject constructor(
    private val repository: OcrRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): OcrResult = repository.recognizeText(bitmap)
}
