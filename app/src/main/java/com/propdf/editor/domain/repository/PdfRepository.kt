package com.propdf.editor.domain.repository

import com.propdf.editor.domain.model.PdfDocument
import kotlinx.coroutines.flow.Flow

interface PdfRepository {
    fun getRecentDocuments(): Flow<List<PdfDocument>>
    suspend fun insertDocument(document: PdfDocument)
    suspend fun deleteDocument(uri: String)
}
