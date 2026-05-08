package com.propdf.editor.data.repository

import com.propdf.editor.data.local.db.ProPDFDatabase
import com.propdf.editor.domain.model.PdfDocument
import com.propdf.editor.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfRepositoryImpl @Inject constructor(
    private val database: ProPDFDatabase
) : PdfRepository {

    override fun getRecentDocuments(): Flow<List<PdfDocument>> = flow {
        emit(emptyList())
    }

    override suspend fun insertDocument(document: PdfDocument) {
        // TODO: Implement with DAO
    }

    override suspend fun deleteDocument(uri: String) {
        // TODO: Implement with DAO
    }

    override fun searchDocuments(query: String): Flow<List<PdfDocument>> = flow {
        emit(emptyList())
    }

    override suspend fun toggleFavorite(uri: String) {
        // TODO: Implement with DAO
    }

    override fun getFavoriteDocuments(): Flow<List<PdfDocument>> = flow {
        emit(emptyList())
    }
}
