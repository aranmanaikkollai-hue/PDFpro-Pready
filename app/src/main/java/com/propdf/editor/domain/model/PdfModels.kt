package com.propdf.editor.domain.model

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri

data class PdfDocument(
    val uri: Uri,
    val name: String,
    val pageCount: Int,
    val path: String? = null,
    val lastOpened: Long = System.currentTimeMillis()
)

data class PdfPage(
    val index: Int,
    val bitmap: Bitmap? = null,
    val width: Int = 0,
    val height: Int = 0
)

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

data class ScanPage(
    val id: Int = 0,
    val bitmap: Bitmap? = null,
    val uri: Uri? = null,
    val filter: ScanFilter = ScanFilter.AUTO
)

enum class ScanFilter {
    AUTO, COLOR, GRAYSCALE, BLACK_WHITE
}

data class Bookmark(
    val id: Long = 0,
    val documentUri: String,
    val pageIndex: Int,
    val label: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
