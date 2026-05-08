package com.propdf.editor.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.domain.model.*
import kotlinx.coroutines.flow.Flow

interface PdfRepository {
    fun getRecentDocuments(): Flow<List<PdfDocument>>
    suspend fun insertDocument(document: PdfDocument)
    suspend fun deleteDocument(uri: String)
    fun searchDocuments(query: String): Flow<List<PdfDocument>>
    suspend fun toggleFavorite(uri: String)
    fun getFavoriteDocuments(): Flow<List<PdfDocument>>
}

interface PdfViewerRepository {
    fun openDocument(uri: Uri): Result<PdfDocument>
    fun renderPage(pageIndex: Int, width: Int, height: Int): Bitmap?
    fun getPageCount(): Int
    fun closeDocument()
    fun pauseRendering()
    fun resumeRendering()
    fun searchInDocument(query: String): List<PdfPage>
}

interface OcrRepository {
    suspend fun recognizeText(bitmap: Bitmap): OcrResult
    suspend fun recognizeTextFromPdf(uri: Uri, pageIndex: Int): OcrResult
}

interface BookmarkRepository {
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun removeBookmark(id: Long)
    fun getBookmarksForDocument(documentUri: String): Flow<List<Bookmark>>
}

interface AnnotationRepository {
    fun getAnnotations(documentUri: String): Flow<List<PdfAnnotation>>
    suspend fun addAnnotation(annotation: PdfAnnotation)
    suspend fun removeAnnotation(id: Long)
    suspend fun updateAnnotation(annotation: PdfAnnotation)
}

interface SettingsRepository {
    fun isDarkModeEnabled(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    fun isLowMemoryModeEnabled(): Flow<Boolean>
    suspend fun setLowMemoryMode(enabled: Boolean)
    fun getDefaultAnnotationColor(): Flow<Int>
    suspend fun setDefaultAnnotationColor(color: Int)
    fun getPerformanceProfile(): Flow<DevicePerformanceProfile>
}

interface PdfOperationsRepository {
    suspend fun mergePdfs(sources: List<Uri>, output: Uri): Result<Unit>
    suspend fun splitPdf(source: Uri, pages: List<Int>, output: Uri): Result<Unit>
    suspend fun compressPdf(source: Uri, output: Uri): Result<Unit>
    suspend fun rotatePages(source: Uri, pages: List<Int>, degrees: Int, output: Uri): Result<Unit>
    suspend fun deletePages(source: Uri, pages: List<Int>, output: Uri): Result<Unit>
    suspend fun addWatermark(source: Uri, text: String, output: Uri): Result<Unit>
    suspend fun encryptPdf(source: Uri, password: String, output: Uri): Result<Unit>
    suspend fun imagesToPdf(images: List<Uri>, output: Uri): Result<Unit>
    suspend fun pdfToImages(source: Uri, outputDir: Uri): Result<List<Uri>>
    suspend fun extractText(source: Uri): Result<String>
}
