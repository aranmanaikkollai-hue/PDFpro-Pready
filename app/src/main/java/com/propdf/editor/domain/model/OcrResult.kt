package com.propdf.editor.domain.model

import android.graphics.Rect

data class OcrResult(
    val text: String,
    val confidence: Float,
    val words: List<OcrWord> = emptyList()
)

data class OcrWord(
    val text: String,
    val confidence: Float,
    val boundingBox: Rect
)
