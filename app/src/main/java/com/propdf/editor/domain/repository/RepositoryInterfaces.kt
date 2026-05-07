package com.propdf.editor.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.domain.model.*
import kotlinx.coroutines.flow.Flow

interface PdfRepository {
    suspend fun getPdfFiles(): Flow<List<PdfFile>>
    suspend fun getRecentFiles(limit: Int = 20): Flow<List<PdfFile>>
    suspend fun getFavoriteFiles(): Flow<List<PdfFile>>
    suspend fun addRecentFile(pdfFile: PdfFile)
    suspend fun removeRecentFile(pdfFile: PdfFile)
    suspend fun toggleFavorite(pdfFile: PdfFile)
    suspend fun searchFiles(query: String): Flow<List<PdfFile>>
}

interface PdfViewerRepository {
    suspend fun openDocument(uri: Uri): Result<PdfDocumentInfo>
    suspend fun renderPage(pageNumber: Int, width: Int, height: Int): Result<Bitmap>
    suspend fun getPageCount(): Int
    suspend fun searchInDocument(query: String): Result<List<SearchResult>>
    suspend fun closeDocument()
}

data class PdfDocumentInfo(
    val pageCount: Int,
    val title: String?,
    val author: String?,
    val creationDate: String?
)

interface BookmarkRepository {
    suspend fun getBookmarks(pdfUri: String): Flow<List<PdfBookmark>>
    suspend fun addBookmark(bookmark: PdfBookmark)
    suspend fun removeBookmark(bookmarkId: Long)
    suspend fun updateBookmark(bookmark: PdfBookmark)
}

interface AnnotationRepository {
    suspend fun getAnnotations(pdfUri: String, pageNumber: Int): List<PdfAnnotation>
    suspend fun addAnnotation(annotation: PdfAnnotation)
    suspend fun removeAnnotation(annotationId: Long)
    suspend fun updateAnnotation(annotation: PdfAnnotation)
}

interface PdfOperationsRepository {
    suspend fun mergePdfs(files: List<Uri>, outputFile: java.io.File): Result<java.io.File>
    suspend fun splitPdf(file: Uri, pageRanges: List<IntRange>, outputDir: java.io.File): Result<List<java.io.File>>
    suspend fun compressPdf(file: Uri, outputFile: java.io.File, quality: Int): Result<java.io.File>
    suspend fun rotatePages(file: Uri, outputFile: java.io.File, rotations: Map<Int, Int>): Result<java.io.File>
    suspend fun deletePages(file: Uri, outputFile: java.io.File, pagesToDelete: List<Int>): Result<java.io.File>
    suspend fun addWatermark(file: Uri, outputFile: java.io.File, text: String): Result<java.io.File>
    suspend fun encryptPdf(file: Uri, outputFile: java.io.File, password: String): Result<java.io.File>
    suspend fun imagesToPdf(images: List<Uri>, outputFile: java.io.File): Result<java.io.File>
    suspend fun pdfToImages(file: Uri, outputDir: java.io.File): Result<List<java.io.File>>
    suspend fun extractText(file: Uri): Result<String>
}

interface OcrRepository {
    suspend fun recognizeText(bitmap: Bitmap): Result<OcrResult>
    suspend fun recognizeTextFromUri(uri: Uri): Result<OcrResult>
    suspend fun isLanguageDataAvailable(language: String): Boolean
    suspend fun downloadLanguageData(language: String): Result<Unit>
}

interface SettingsRepository {
    suspend fun isDarkModeEnabled(): Boolean
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun isLowMemoryModeEnabled(): Boolean
    suspend fun setLowMemoryMode(enabled: Boolean)
    suspend fun getDefaultAnnotationColor(): Int
    suspend fun setDefaultAnnotationColor(color: Int)
    suspend fun getPerformanceProfile(): DevicePerformanceProfile
}
