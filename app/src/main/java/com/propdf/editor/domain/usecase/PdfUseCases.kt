package com.propdf.editor.domain.usecase

import com.propdf.editor.domain.model.PdfDocument
import com.propdf.editor.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentDocumentsUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(): Flow<List<PdfDocument>> = repository.getRecentDocuments()
}

class GetFavoriteDocumentsUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(): Flow<List<PdfDocument>> = repository.getFavoriteDocuments()
}

class InsertDocumentUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(document: PdfDocument) = repository.insertDocument(document)
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(uri: String) = repository.toggleFavorite(uri)
}

class SearchDocumentsUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(query: String): Flow<List<PdfDocument>> = repository.searchDocuments(query)
}
