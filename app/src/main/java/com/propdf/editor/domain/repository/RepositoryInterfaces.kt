package com.propdf.editor.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.propdf.editor.domain.model.Bookmark
import com.propdf.editor.domain.model.OcrResult
import com.propdf.editor.domain.model.PdfDocument
import com.propdf.editor.domain.model.PdfPage
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
    suspend fun recognizeText(bitmap: android.graphics.Bitmap): OcrResult
    suspend fun recognizeTextFromPdf(uri: Uri, pageIndex: Int): OcrResult
}

interface BookmarkRepository {
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun removeBookmark(id: Long)
    fun getBookmarksForDocument(documentUri: String): Flow<List<Bookmark>>
}
