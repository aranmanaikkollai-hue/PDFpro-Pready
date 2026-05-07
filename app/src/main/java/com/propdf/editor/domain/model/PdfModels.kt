package com.propdf.editor.domain.model

import android.net.Uri

sealed class PdfSource {
    data class ContentUri(val uri: Uri) : PdfSource()
    data class FilePath(val path: String) : PdfSource()
}

data class PdfFile(
    val id: Long = 0,
    val name: String,
    val uri: String,
    val path: String? = null,
    val size: Long = 0,
    val pageCount: Int = 0,
    val lastModified: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val thumbnailPath: String? = null
) {
    fun getSource(): PdfSource {
        return if (path != null) PdfSource.FilePath(path) else PdfSource.ContentUri(Uri.parse(uri))
    }
}

data class PdfPage(
    val pageNumber: Int,
    val width: Int,
    val height: Int
)

data class PdfBookmark(
    val id: Long = 0,
    val pdfUri: String,
    val pageNumber: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PdfAnnotation(
    val id: Long = 0,
    val pdfUri: String,
    val pageNumber: Int,
    val type: AnnotationType,
    val color: Int,
    val data: String, // JSON serialized data
    val timestamp: Long = System.currentTimeMillis()
)

enum class AnnotationType {
    HIGHLIGHT,
    UNDERLINE,
    STRIKEOUT,
    INK,
    TEXT,
    FREE_TEXT
}

data class SearchResult(
    val pageNumber: Int,
    val text: String,
    val bounds: List<Float>
)

data class OcrResult(
    val text: String,
    val confidence: Float,
    val blocks: List<TextBlock>
)

data class TextBlock(
    val text: String,
    val boundingBox: android.graphics.Rect
)

data class DevicePerformanceProfile(
    val isLowEnd: Boolean,
    val renderQuality: Int,
    val enableAnimations: Boolean,
    val useCompose: Boolean,
    val maxConcurrentOperations: Int
)
